package com.hanshi.weather.query.service.impl;

import com.hanshi.weather.common.constant.WeatherConstant;
import com.hanshi.weather.common.domain.WeatherDayData;
import com.hanshi.weather.common.domain.WeatherResponse;
import com.hanshi.weather.common.domain.WeatherResult;
import com.hanshi.weather.query.service.WeatherService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 天气查询服务实现
 * <p>
 * 核心逻辑：
 * 1. 先查Redis缓存 → 命中直接返回
 * 2. 缓存未命中 → 双重检查锁 → 调第三方API → 写入Redis → 返回
 * 3. 第三方API超时/异常 → Hystrix熔断降级 → 返回兜底数据
 */
@Service
public class WeatherServiceImpl implements WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 直调第三方API（不读缓存，CacheJob定时同步用）
     */
    @Override
    @HystrixCommand(fallbackMethod = "queryByApiFallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")
            })
    public WeatherResponse queryWeatherByApi(String cityId) {
        log.info("直调第三方API: cityId={}", cityId);
        String url = WeatherConstant.WEATHER_API
                + "key=" + WeatherConstant.KEY
                + "&city=" + cityId
                + "&type=" + WeatherConstant.QUERY_TYPE;
        return restTemplate.getForObject(url, WeatherResponse.class);
    }

    /**
     * 先查Redis → miss则双重检查 → 调API → 写缓存
     */
    @Override
    @HystrixCommand(fallbackMethod = "queryByCacheFallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")
            })
    public WeatherResponse findWeatherByCityId(String cityId) {
        // 1. 查Redis缓存
        String cacheKey = WeatherConstant.WEATHER_TABLE_NAME + ":" + cityId;
        WeatherResponse result = (WeatherResponse) redisTemplate.opsForValue().get(cacheKey);

        if (result == null) {
            // 2. 双重检查锁
            synchronized (this) {
                result = (WeatherResponse) redisTemplate.opsForValue().get(cacheKey);
                if (result == null) {
                    log.info("缓存未命中，调第三方API: cityId={}", cityId);
                    String url = WeatherConstant.WEATHER_API
                            + "key=" + WeatherConstant.KEY
                            + "&city=" + cityId
                            + "&type=" + WeatherConstant.QUERY_TYPE;
                    result = restTemplate.getForObject(url, WeatherResponse.class);

                    if (result != null) {
                        // 3. 写入缓存，2小时过期
                        redisTemplate.opsForValue().set(cacheKey, result,
                                WeatherConstant.REDIS_EXPIRE_SECONDS, TimeUnit.SECONDS);
                    }
                }
            }
        } else {
            log.info("缓存命中: cityId={}", cityId);
        }

        return result;
    }

    // ===== Hystrix降级方法 =====

    /**
     * queryWeatherByApi 的降级：第三方API不可用时返回提示
     */
    public WeatherResponse queryByApiFallback(String cityId) {
        log.warn("Hystrix降级(queryByApi): cityId={}，第三方API不可用", cityId);
        return buildFallbackResponse("第三方天气服务暂时不可用，请稍后再试");
    }

    /**
     * findWeatherByCityId 的降级
     */
    public WeatherResponse queryByCacheFallback(String cityId) {
        log.warn("Hystrix降级(findWeather): cityId={}，服务繁忙", cityId);

        // 尝试从Redis获取旧数据（可能已过期）
        String cacheKey = WeatherConstant.WEATHER_TABLE_NAME + ":" + cityId;
        WeatherResponse cached = (WeatherResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("降级返回已过期缓存: cityId={}", cityId);
            return cached;
        }

        return buildFallbackResponse("天气服务繁忙，请稍后再试");
    }

    /**
     * 构建兜底响应
     */
    private WeatherResponse buildFallbackResponse(String msg) {
        WeatherResponse response = new WeatherResponse();
        response.setCode(500);
        response.setMsg(msg);

        WeatherResult result = new WeatherResult();
        result.setArea("未知");

        List<WeatherDayData> list = new ArrayList<>();
        WeatherDayData today = new WeatherDayData();
        today.setDate("--");
        today.setWeek("--");
        today.setWeather("暂无数据");
        today.setLowest("--");
        today.setHighest("--");
        today.setWind("--");
        today.setWindsc("--");
        today.setTips(msg);
        today.setPcpn("0");
        list.add(today);
        result.setList(list);
        response.setResult(result);

        return response;
    }
}

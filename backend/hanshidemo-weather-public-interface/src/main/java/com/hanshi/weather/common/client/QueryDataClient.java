package com.hanshi.weather.common.client;

import com.hanshi.weather.common.domain.WeatherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Feign客户端：调用 querydata9200 天气查询服务
 *
 * 服务名 = 该微服务在Eureka上注册的 spring.application.name
 */
@FeignClient("hanshidemo-weather-querydata9200")
public interface QueryDataClient {

    /** 直调第三方API（获取最新鲜数据，CacheJob用） */
    @RequestMapping("/query/weather/api/{cityId}")
    WeatherResponse findWeatherByApi(@PathVariable("cityId") String cityId);

    /** 先查Redis缓存再调API（用户端用） */
    @RequestMapping("/query/weather/cache/{cityId}")
    WeatherResponse findWeatherByUser(@PathVariable("cityId") String cityId);
}

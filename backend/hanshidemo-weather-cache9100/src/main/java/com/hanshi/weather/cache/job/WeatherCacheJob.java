package com.hanshi.weather.cache.job;

import com.hanshi.weather.common.client.CityListClient;
import com.hanshi.weather.common.client.QueryDataClient;
import com.hanshi.weather.common.constant.WeatherConstant;
import com.hanshi.weather.common.domain.TabCity;
import com.hanshi.weather.common.domain.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 定时缓存天气数据到Redis（多线程并行版）
 * <p>
 * 改造前：for循环串行拉取6个城市 → 耗时 ≈ 6 × 单次API耗时
 * 改造后：线程池并行拉取6个城市 → 耗时 ≈ 1 × 单次API耗时（快 ~5倍）
 * <p>
 * 流程：
 * 1. Feign调CityList(9300) → 获取全部城市
 * 2. 多线程并行调QueryData(9200) → 每个城市一个Future
 * 3. CompletableFuture.allOf() 等待全部完成 → 写入Redis
 */
@Component
public class WeatherCacheJob {

    private static final Logger log = LoggerFactory.getLogger(WeatherCacheJob.class);

    @Autowired
    private CityListClient cityListClient;

    @Autowired
    private QueryDataClient queryDataClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    @Qualifier("weatherCacheExecutor")
    private ThreadPoolTaskExecutor executor;

    /**
     * 每5分钟执行一次：多线程并行拉取所有城市的天气数据，缓存到Redis
     * cron: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0/1 * * * ?")  // 测试：每1分钟; 正式: 0/5
    public void syncWeatherCache() {
        long startTime = System.currentTimeMillis();
        log.info("========== 定时任务开始(多线程) ========== {}", new Date());

        try {
            // 1. 获取城市列表
            List<TabCity> cityList = cityListClient.getCityList();
            log.info("获取到{}个城市，准备并行拉取", cityList != null ? cityList.size() : 0);

            if (cityList == null || cityList.isEmpty()) {
                log.warn("城市列表为空，跳过本次同步");
                return;
            }

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            // 2. 每个城市提交一个异步任务到线程池
            for (TabCity city : cityList) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        log.debug("线程[{}]开始拉取: {} ({})",
                                Thread.currentThread().getName(), city.getCity(), city.getCityid());

                        WeatherResponse weather = queryDataClient.findWeatherByApi(city.getCityid());

                        if (weather != null && weather.getResult() != null) {
                            String cacheKey = WeatherConstant.WEATHER_TABLE_NAME + ":" + city.getCityid();
                            redisTemplate.opsForValue().set(cacheKey, weather,
                                    WeatherConstant.REDIS_EXPIRE_SECONDS, TimeUnit.SECONDS);
                            successCount.incrementAndGet();
                            log.debug("线程[{}]缓存成功: {} ({})",
                                    Thread.currentThread().getName(), city.getCity(), city.getCityid());
                        } else {
                            failCount.incrementAndGet();
                            log.warn("天气数据为空: {} ({})", city.getCity(), city.getCityid());
                        }
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                        log.error("线程[{}]缓存失败: {} ({}) - {}",
                                Thread.currentThread().getName(), city.getCity(), city.getCityid(), e.getMessage());
                    }
                }, executor);  // 👈 使用自定义线程池（不使用默认ForkJoinPool）

                futures.add(future);
            }

            // 3. 等待所有城市拉取完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            long cost = System.currentTimeMillis() - startTime;
            log.info("定时任务完成(多线程): 成功{}个, 失败{}个, 共{}个, 耗时{}ms",
                    successCount.get(), failCount.get(), cityList.size(), cost);

        } catch (Exception e) {
            log.error("定时任务异常: {}", e.getMessage(), e);
        }

        log.info("========== 定时任务结束 ==========");
    }
}

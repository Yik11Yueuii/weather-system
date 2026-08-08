package com.hanshi.weather.cache.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步线程池配置 — 用于CacheJob并行拉取多个城市的天气数据
 *
 * 线程池参数：
 *   核心线程数 = 城市数量（6）
 *   最大线程数 = 核心 × 2
 *   队列容量 = 10
 *   拒绝策略 = CallerRunsPolicy（忙不过来交给调用线程，不丢任务）
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);

    @Bean("weatherCacheExecutor")
    public ThreadPoolTaskExecutor weatherCacheExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(6);
        executor.setMaxPoolSize(12);
        executor.setQueueCapacity(10);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("weather-cache-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();

        log.info("天气缓存线程池已初始化: core=6, max=12, queue=10");
        return executor;
    }
}

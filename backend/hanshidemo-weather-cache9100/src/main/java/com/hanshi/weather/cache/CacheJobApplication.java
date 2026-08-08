package com.hanshi.weather.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 天气缓存定时微服务 — 端口9100
 * 职责：定时通过Feign调用 CityList(9300) + QueryData(9200)，把最新天气写入Redis
 */
@SpringBootApplication(scanBasePackages = {"com.hanshi.weather.cache", "com.hanshi.weather.common"})
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.hanshi.weather.common.client"})
@EnableScheduling
public class CacheJobApplication {
    public static void main(String[] args) {
        SpringApplication.run(CacheJobApplication.class, args);
    }
}

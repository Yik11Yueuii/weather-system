package com.hanshi.weather.query;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

/**
 * 天气查询微服务 — 端口9200
 * 职责：提供天气数据查询（Redis缓存 + 第三方API + Hystrix熔断）
 */
@SpringBootApplication(scanBasePackages = {"com.hanshi.weather.query", "com.hanshi.weather.common"})
@EnableEurekaClient
@EnableHystrix
public class QueryDataApplication {
    public static void main(String[] args) {
        SpringApplication.run(QueryDataApplication.class, args);
    }
}

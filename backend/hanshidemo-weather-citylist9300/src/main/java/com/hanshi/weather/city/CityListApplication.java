package com.hanshi.weather.city;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 城市列表微服务 — 端口9300
 * 职责：提供城市数据的CRUD接口，数据源MySQL + Redis缓存
 */
@SpringBootApplication(scanBasePackages = {"com.hanshi.weather.city", "com.hanshi.weather.common"})
@EnableEurekaClient
@MapperScan("com.hanshi.weather.city.mapper")
public class CityListApplication {
    public static void main(String[] args) {
        SpringApplication.run(CityListApplication.class, args);
    }
}

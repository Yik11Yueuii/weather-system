package com.huawei.pro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HanshidemoServiceProvider8002Application {

    public static void main(String[] args) {
        SpringApplication.run(HanshidemoServiceProvider8002Application.class, args);
    }
}
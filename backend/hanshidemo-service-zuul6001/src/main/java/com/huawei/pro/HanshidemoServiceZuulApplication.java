package com.huawei.pro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy //网关也是一个客户端，需要注册，需要获取微服务的注册表
public class HanshidemoServiceZuulApplication {
    public static void main(String[] args) {
        SpringApplication.run(HanshidemoServiceZuulApplication.class, args);
    }
}
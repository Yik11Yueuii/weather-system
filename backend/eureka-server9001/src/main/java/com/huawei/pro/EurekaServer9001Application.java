package com.huawei.pro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer   //告诉SpringBoot  当前工程是一个注册中心
public class EurekaServer9001Application {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServer9001Application.class, args);
    }

}

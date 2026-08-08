package com.huawei.pro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients  //开启使用Feign的注解  必须要有
@EnableEurekaClient
public class HanshidemoServiceFeign8201Application {

    public static void main(String[] args) {
        SpringApplication.run(HanshidemoServiceFeign8201Application.class,args);
    }
}

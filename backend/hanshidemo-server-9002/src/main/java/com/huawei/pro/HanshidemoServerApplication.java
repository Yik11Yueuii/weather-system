package com.huawei.pro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class HanshidemoServerApplication {
    public static void main(String[] args){
        SpringApplication.run(HanshidemoServerApplication.class,args);
    }
}

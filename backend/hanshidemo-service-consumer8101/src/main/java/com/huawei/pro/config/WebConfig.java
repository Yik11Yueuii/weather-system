package com.huawei.pro.config;

import com.netflix.loadbalancer.RandomRule;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {
    @Bean
    @LoadBalanced //负载均衡 做人做事要公平 调用其他服务时要公平
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}


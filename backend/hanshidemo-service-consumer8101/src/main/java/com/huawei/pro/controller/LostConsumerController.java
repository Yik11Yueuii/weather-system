package com.huawei.pro.controller;

import com.huawei.pro.domain.LostItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class LostConsumerController {

    /**
     * Spring自动注入RestTemplate
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 调用Provider查询所有失物
     *
     * 浏览器访问：
     * http://localhost:8101/lost/list
     */
    @GetMapping("/lost/list")
    public LostItem[] getLostList(){

        return restTemplate.getForObject(
                "http://HANSHIDEMO-SERVICE-PROVIDER8002/lost/list",
                LostItem[].class
        );

    }

}
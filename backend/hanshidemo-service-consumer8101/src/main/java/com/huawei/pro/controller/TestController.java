package com.huawei.pro.controller;

import com.huawei.pro.domain.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TestController {

    //要调用的服务的名字
    private static final String SERVICE_PROVIDER_NAME="http://HANSHIDEMO-SERVICE-PROVIDER8002";


    //依赖注入
    @Autowired
    RestTemplate restTemplate;

    //注意：这里跟平时写接口不同：今天是一个服务调用另外一个服务，使用的协议是rpc：远程接口调用
    @RequestMapping("/createOrder")
    public Users createOrder()throws Exception{
        //先登录
        /*

         @RequestMapping("/login/{username}")  //严格要求客户端提交为表达提交
    public Users login(@PathVariable("username") String username) {

         */

        // restTemplate.getForObject(SERVICE_PROVIDER_NAME+"/login/zhangsan", Users.class,null);
        Users user=restTemplate.postForObject(SERVICE_PROVIDER_NAME+"/login/zhangsan", null, Users.class);

        //创建订单

        return user;
    }

}

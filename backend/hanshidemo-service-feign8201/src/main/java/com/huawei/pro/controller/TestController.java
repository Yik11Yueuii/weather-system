package com.huawei.pro.controller;

import com.huawei.pro.client.UserClientService;
import com.huawei.pro.domain.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private UserClientService userClientService;

    //这是消费者的控制器
    //注意：这里跟平时写接口不同；今天是一个服务调用另外一个服务，今天是Feign的实现
    @RequestMapping("/createOrder")
    public Users createOrder() throws Exception{
        System.out.println("userClientService:"+userClientService);
        Users user=null;
        user=userClientService.login("zhangsan");
        System.out.println("返回的user的username："+user.getUsername());
        return user;
    }

    //测试服务提供者的register方法
    @RequestMapping("/testRegister")
    public Users testRegister(){
        Users user=new Users();
        user.setUsername("不天真");
        user.setPassword("123456");

        user=userClientService.register(user);
        return user;  //这个user名字和密码后面都加上了端口号
    }
}
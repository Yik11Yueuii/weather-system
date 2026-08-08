package com.huawei.pro.controller;

import com.huawei.pro.dto.Users;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    /**
     * 模拟登录接口（供 Feign 调用）
     */
    @RequestMapping("/login/{username}")
    public Users login(@PathVariable("username") String username){

        Users user = new Users();
        user.setUsername(username);
        user.setPassword("123456");

        return user;
    }

}
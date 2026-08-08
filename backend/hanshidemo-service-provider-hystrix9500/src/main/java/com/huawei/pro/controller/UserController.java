package com.huawei.pro.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import domain.Users;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Value("${server.port}") //spring4.0之后有一些新的注解你们要掌握
    int port;

    //前端传给后端：json对象--》java对象（这样后端才能正常使用）@RequestBody 搞定
    //后端传给前端：java对象---json对象（这样前端才能正常使用）@ResponseBody搞定
    //@PostMapping + @RequestBody  从软件安全的角度考虑：用户数据不能跟在url的后面，应该放在请求体中
    @PostMapping("/register")
    @HystrixCommand(fallbackMethod = "registerCallBack") //兜底的方法
    public Users register(@RequestBody Users user) {

        //模拟代码运行期间会抛出异常
        if("".equals(user.getPassword()) || user.getPassword()==null){
            throw new RuntimeException("密码为空");
        }
        user.setUsername(user.getUsername()+"-"+port);
        user.setPassword("123456-"+port);
        System.out.println("服务提供者输出：user:"+user.getUsername()+","+user.getPassword());
        return user;

    }
    //因为要多写代码，增加工作量，所以并不是很多企业愿意做这个备份方法的定义
    public Users registerCallback(@RequestBody Users user) {

        user.setUsername(user.getUsername()+"-"+port+",兜底");
        user.setPassword("666666-"+port+",兜底");
        return user;

    }
}

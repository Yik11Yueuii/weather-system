package com.huawei.pro.client;

import com.huawei.pro.domain.Users;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

//此接口假装名字叫做hanshidemo-service-provider8002的微服务
@FeignClient(value = "HANSHIDEMO-SERVICE-PROVIDER-HYSTRIX9500",fallbackFactory = UserClientServiceFallback.class)
//@Service 不需要加 开发遵循代码最少原则 导包也要遵循包最少原则
public interface UserClientService {

    @RequestMapping("/login/{username}")  //严格要求客户端提交为表达提交
    public Users login(@PathVariable("username") String username);

    @PostMapping("/register")
    public Users register(@RequestBody Users user);
}
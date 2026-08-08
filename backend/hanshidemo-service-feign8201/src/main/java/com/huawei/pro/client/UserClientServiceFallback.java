package com.huawei.pro.client;

import com.huawei.pro.domain.Users;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class UserClientServiceFallback implements FallbackFactory<UserClientService> {
    @Override
    public UserClientService create(Throwable throwable) {

        return new UserClientService() {

            public Users login(String username){
                Users user=new Users();
                user.setUsername("兜底1");
                user.setPassword("兜底密码1-123456");
                return user;
            }


            public Users register(Users user){
                Users user_register=new Users();
                user_register.setUsername("兜底2");
                user_register.setPassword("兜底密码2-123456");
                return user_register;
            }
        };
    }
}

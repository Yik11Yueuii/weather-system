package com.hanshi.weather.common.client;

import com.hanshi.weather.common.domain.TabCity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Feign客户端：调用 citylist9300 城市列表服务
 *
 * 服务名 = 该微服务在Eureka上注册的 spring.application.name
 */
@FeignClient("hanshidemo-weather-citylist9300")
public interface CityListClient {

    /** 获取全部城市列表 */
    @RequestMapping("/city/list")
    List<TabCity> getCityList();

    /** 根据ID查询单个城市 */
    @RequestMapping("/city/{id}")
    TabCity getCityById(@PathVariable("id") Long id);
}

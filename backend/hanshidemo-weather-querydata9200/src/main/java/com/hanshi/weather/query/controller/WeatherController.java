package com.hanshi.weather.query.controller;

import com.hanshi.weather.common.domain.WeatherResponse;
import com.hanshi.weather.query.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 天气查询控制器
 * <p>
 * /query/weather/api/{cityId}    → 直调第三方API（CacheJob用）
 * /query/weather/cache/{cityId}  → 先Redis后API（用户端用）
 */
@RestController
@RequestMapping("/query/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    /** 直调第三方API，获取最新天气数据 */
    @RequestMapping("/api/{cityId}")
    public WeatherResponse findByApi(@PathVariable("cityId") String cityId) {
        return weatherService.queryWeatherByApi(cityId);
    }

    /** 先查缓存再调API（用户端入口） */
    @RequestMapping("/cache/{cityId}")
    public WeatherResponse findByUser(@PathVariable("cityId") String cityId) {
        return weatherService.findWeatherByCityId(cityId);
    }
}

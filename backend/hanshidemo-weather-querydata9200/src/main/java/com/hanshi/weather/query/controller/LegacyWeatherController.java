package com.hanshi.weather.query.controller;

import com.hanshi.weather.common.domain.WeatherResponse;
import com.hanshi.weather.query.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 兼容旧单体架构路径 /weather/{city}
 * Zuul 剥离 /weatherhanshi 前缀后，转发到本控制器的 /weather/{city}
 */
@RestController
@RequestMapping("/weatherhanshi/weather")
public class LegacyWeatherController {

    @Autowired
    private WeatherService weatherService;

    /** 旧路径兼容 → 等价于 /query/weather/cache/{city} */
    @RequestMapping("/{city}")
    public WeatherResponse weather(@PathVariable("city") String city) {
        return weatherService.findWeatherByCityId(city);
    }
}

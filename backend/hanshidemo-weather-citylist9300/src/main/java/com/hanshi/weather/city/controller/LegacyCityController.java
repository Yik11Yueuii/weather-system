package com.hanshi.weather.city.controller;

import com.hanshi.weather.city.service.ICityService;
import com.hanshi.weather.common.domain.TabCity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 兼容旧单体架构路径 /tabCity/CityList
 * Zuul 剥离 /weatherhanshi 前缀后，转发到 /tabCity/CityList
 */
@RestController
@RequestMapping("/weatherhanshi/tabCity")
public class LegacyCityController {

    @Autowired
    private ICityService cityService;

    @RequestMapping("/CityList")
    public List<TabCity> getTabCityList() {
        return cityService.listAll();
    }
}

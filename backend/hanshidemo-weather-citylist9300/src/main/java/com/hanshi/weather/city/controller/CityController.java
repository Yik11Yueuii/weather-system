package com.hanshi.weather.city.controller;

import com.hanshi.weather.city.service.ICityService;
import com.hanshi.weather.common.domain.TabCity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 城市列表控制器
 * <p>
 * /city/list  → 获取全部城市（前端调用）
 * /city/{id}  → 根据ID查城市
 * /city/add   → 新增城市（内部管理）
 * /city/{id}  → 删除城市（内部管理）
 */
@RestController
@RequestMapping("/city")
public class CityController {

    @Autowired
    private ICityService cityService;

    /** 获取全部城市列表 */
    @GetMapping("/list")
    public List<TabCity> getCityList() {
        return cityService.listAll();
    }

    /** 根据ID查单个城市 */
    @GetMapping("/{id}")
    public TabCity getCityById(@PathVariable("id") Long id) {
        return cityService.getById(id);
    }

    /** 新增城市 */
    @PostMapping("/add")
    public String addCity(@RequestBody TabCity city) {
        boolean ok = cityService.addCity(city);
        return ok ? "新增成功" : "新增失败";
    }

    /** 删除城市 */
    @DeleteMapping("/{id}")
    public String deleteCity(@PathVariable("id") Long id) {
        boolean ok = cityService.deleteCity(id);
        return ok ? "删除成功" : "删除失败";
    }
}

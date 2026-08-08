package com.hanshi.weather.city.service;

import com.hanshi.weather.common.domain.TabCity;

import java.util.List;

/**
 * 城市服务接口
 */
public interface ICityService {

    /** 获取全部城市 */
    List<TabCity> listAll();

    /** 根据ID查城市 */
    TabCity getById(Long id);

    /** 新增城市 */
    boolean addCity(TabCity city);

    /** 删除城市 */
    boolean deleteCity(Long id);
}

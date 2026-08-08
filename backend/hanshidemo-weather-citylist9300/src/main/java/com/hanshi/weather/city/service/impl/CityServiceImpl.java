package com.hanshi.weather.city.service.impl;

import com.hanshi.weather.city.mapper.TabCityMapper;
import com.hanshi.weather.city.service.ICityService;
import com.hanshi.weather.common.domain.TabCity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 城市服务实现 — Redis缓存城市列表，减轻DB压力
 */
@Service
public class CityServiceImpl implements ICityService {

    private static final Logger log = LoggerFactory.getLogger(CityServiceImpl.class);

    @Autowired
    private TabCityMapper tabCityMapper;

    /** Redis缓存名 */
    private static final String CITY_LIST_CACHE = "city_list_cache";

    @Cacheable(value = CITY_LIST_CACHE, key = "'all'")
    @Override
    public List<TabCity> listAll() {
        log.info("从数据库查询全部城市");
        return tabCityMapper.selectList(null);
    }

    @Override
    public TabCity getById(Long id) {
        return tabCityMapper.selectById(id);
    }

    @CacheEvict(value = CITY_LIST_CACHE, key = "'all'")
    @Override
    public boolean addCity(TabCity city) {
        int rows = tabCityMapper.insert(city);
        return rows > 0;
    }

    @CacheEvict(value = CITY_LIST_CACHE, key = "'all'")
    @Override
    public boolean deleteCity(Long id) {
        int rows = tabCityMapper.deleteById(id);
        return rows > 0;
    }
}

package com.hanshi.weather.query.service;

import com.hanshi.weather.common.domain.WeatherResponse;

/**
 * 天气查询服务接口
 */
public interface WeatherService {

    /** 直调第三方API（CacheJob获取最新数据用） */
    WeatherResponse queryWeatherByApi(String cityId);

    /** 先查Redis缓存 → miss则调API → 写入缓存（用户端用） */
    WeatherResponse findWeatherByCityId(String cityId);
}

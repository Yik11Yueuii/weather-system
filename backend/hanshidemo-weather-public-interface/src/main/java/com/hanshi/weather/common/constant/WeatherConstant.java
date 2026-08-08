package com.hanshi.weather.common.constant;

/**
 * 天气服务常量类
 */
public class WeatherConstant {

    /** 天行API接口地址 */
    public static final String WEATHER_API = "https://apis.tianapi.com/tianqi/index?";

    /** 天行API Key */
    public static final String KEY = System.getenv("TIANAPI_KEY");

    /** 查询类型：7日预报 */
    public static final Integer QUERY_TYPE = 7;

    /** Redis缓存天气数据的Hash表名 */
    public static final String WEATHER_TABLE_NAME = "weather_cache";

    /** Redis缓存过期时间（秒）：2小时 */
    public static final long REDIS_EXPIRE_SECONDS = 7200;

    /** Auth请求头名称 */
    public static final String AUTH_HEADER = "X-Internal-Token";

    /** 内部调用Token */
    public static final String INTERNAL_TOKEN = System.getenv("WEATHER_INTERNAL_TOKEN");
}

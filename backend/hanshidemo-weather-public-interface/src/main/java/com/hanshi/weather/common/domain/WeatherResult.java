package com.hanshi.weather.common.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 天气结果体（包含省份、城市、7日预报列表）
 */
public class WeatherResult implements Serializable {

    private String province;
    private String area;
    private String areaid;
    private List<WeatherDayData> list;

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getAreaid() { return areaid; }
    public void setAreaid(String areaid) { this.areaid = areaid; }

    public List<WeatherDayData> getList() { return list; }
    public void setList(List<WeatherDayData> list) { this.list = list; }
}

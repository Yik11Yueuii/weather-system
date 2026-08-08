package com.hanshi.weather.common.domain;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 城市实体（对应 tab_city_copy 表）
 */
@TableName("tab_city_copy")
public class TabCity implements Serializable {

    private Long id;
    private String cityid;
    private String city;
    private String father;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCityid() { return cityid; }
    public void setCityid(String cityid) { this.cityid = cityid; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getFather() { return father; }
    public void setFather(String father) { this.father = father; }
}

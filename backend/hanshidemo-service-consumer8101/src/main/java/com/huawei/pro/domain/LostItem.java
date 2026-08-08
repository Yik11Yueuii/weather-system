package com.huawei.pro.domain;

import lombok.Data;

@Data
public class LostItem {

    private Integer id;

    private String title;

    private String image;

    private String description;

    private String location;

    private String phone;

    private String status;

}
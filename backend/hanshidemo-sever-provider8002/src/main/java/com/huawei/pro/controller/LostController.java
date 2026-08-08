package com.huawei.pro.controller;

import com.huawei.pro.dto.LostItem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LostController {

    /**
     * 模拟数据库
     * 企业里这里应该查询MySQL
     * 现在先用List模拟数据库，方便学习微服务
     */
    private static final List<LostItem> LOST_LIST = new ArrayList<>();

    /**
     * 静态代码块
     * 项目启动时自动执行一次
     * 相当于数据库里已经有两条数据
     */
    static {

        LostItem item1 = new LostItem();
        item1.setId(1);
        item1.setTitle("黑色钱包");
        item1.setImage("wallet.jpg");
        item1.setDescription("里面有身份证和银行卡");
        item1.setLocation("图书馆一楼");
        item1.setPhone("13800138000");
        item1.setStatus("未认领");

        LostItem item2 = new LostItem();
        item2.setId(2);
        item2.setTitle("AirPods");
        item2.setImage("airpods.jpg");
        item2.setDescription("白色无线耳机");
        item2.setLocation("第二食堂");
        item2.setPhone("13800138001");
        item2.setStatus("未认领");

        LOST_LIST.add(item1);
        LOST_LIST.add(item2);
    }

    /**
     * 查询全部失物
     * 浏览器：
     * http://localhost:8002/lost/list
     */
    @GetMapping("/lost/list")
    public List<LostItem> getLostList() {

        return LOST_LIST;

    }

}
package com.wk.kill.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

// 自动get 和 set
@Data
public class ItemKill {

    private Integer id;


    private Integer itemId;


    private Integer total;

    // 格式化时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;


    private Byte isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    // 后加的两个字段
    private String itemName;
    // 采用服务器时间控制是否进行抢购
    private Integer canKill;


}
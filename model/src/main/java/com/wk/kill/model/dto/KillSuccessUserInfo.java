package com.wk.kill.model.dto;


import com.wk.kill.model.entity.ItemKillSuccess;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author:debug (SteadyJack)
 * @Date: 2019/6/21 22:02
 **/
@Data
public class KillSuccessUserInfo extends ItemKillSuccess implements Serializable{

    private String userName;

    private String phone;

    private String email;

    private String itemName;

    @Override
    public String toString() {
        return super.toString()+"\nKillSuccessUserInfo{" +
                "userName='" + userName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", itemName='" + itemName + '\'' +
                '}';
    }
}
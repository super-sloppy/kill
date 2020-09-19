package com.wk.kill.server.service;

import com.wk.kill.model.entity.ItemKill;

import java.util.List;

public interface IItemService {

    List<ItemKill> getKillItems() throws Exception;

    ItemKill getKillDetail(Integer id) throws Exception;

    ItemKill selectByPrimaryKey(Integer id);

//    void commonRecordKillSuccessInfo(ItemKill kill, Integer userId) throws Exception;
}

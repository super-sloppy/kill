package com.wk.kill.server.service.Impl;

import com.wk.kill.model.entity.ItemKill;
import com.wk.kill.model.entity.ItemKillSuccess;
import com.wk.kill.model.mapper.ItemKillMapper;
import com.wk.kill.server.service.IItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ItemService implements IItemService {

    private static final Logger Log = LoggerFactory.getLogger(ItemService.class);

    @Resource
    private ItemKillMapper itemKillMapper;


    /**
     * 获取待秒杀商品列表
     * @return
     * @throws Exception
     */
    public List<ItemKill> getKillItems() throws Exception {
        return itemKillMapper.selectAll();
    }

    public ItemKill getKillDetail(Integer id) throws Exception {
        ItemKill entity = itemKillMapper.selectById(id);
        if (entity == null){
            throw new Exception("获取的秒杀详情不存在");
        }
        return entity;
    }

    public ItemKill selectByPrimaryKey(Integer id) {
        return itemKillMapper.selectByPrimaryKey(id);
    }







}

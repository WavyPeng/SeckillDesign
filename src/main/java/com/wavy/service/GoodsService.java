package com.wavy.service;

import com.wavy.dao.GoodsDao;
import com.wavy.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;

    /**
     * 获取商品列表
     * @return
     */
    public List<GoodsVo> getGoodsVoList(){
        return goodsDao.getGoodsVoList();
    }

    /**
     * 通过goodsId获取商品详情
     * @return
     */
    public GoodsVo getGoodsVoByGoodsId(){
        return goodsDao.getGoodsVoByGoodsId();
    }
}
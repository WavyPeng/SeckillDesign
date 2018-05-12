package com.wavy.service;

import com.wavy.entity.OrderInfo;
import com.wavy.entity.User;
import com.wavy.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeckillService {
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    /**
     * 秒杀
     * @@Transactional 事务处理 保证原子操作
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    public OrderInfo seckill(User user, GoodsVo goods){
        //减库存
        goodsService.reduceStock(goods);
        //下订单 记录秒杀订单
        return orderService.createOrder(user,goods);
    }

}
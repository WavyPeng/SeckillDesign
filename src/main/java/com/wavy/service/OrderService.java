package com.wavy.service;

import com.wavy.dao.OrderDao;
import com.wavy.entity.OrderInfo;
import com.wavy.entity.SeckillOrder;
import com.wavy.entity.User;
import com.wavy.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
public class OrderService {
    @Autowired
    OrderDao orderDao;

    /**
     * 获取秒杀订单
     * @param userId
     * @param goodsId
     * @return
     */
    public SeckillOrder getSeckillOrderByUserIdGoodsId(long userId,long goodsId){
        return orderDao.getSeckillOrderByUserIdGoodsId(userId,goodsId);
    }

    /**
     * 创建订单
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    public OrderInfo createOrder(User user, GoodsVo goods){
        //创建订单记录
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getSeckillPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);   //订单状态最好使用枚举类型来表示
        orderInfo.setUserId(user.getId());
        long orderId = orderDao.insertOrder(orderInfo);

        //创建秒杀订单记录
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderId);
        seckillOrder.setUserId(user.getId());
        orderDao.insertSeckillOrder(seckillOrder);

        return orderInfo;
    }
}
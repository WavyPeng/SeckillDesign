package com.wavy.service;

import com.wavy.dao.OrderDao;
import com.wavy.entity.OrderInfo;
import com.wavy.entity.SeckillOrder;
import com.wavy.entity.User;
import com.wavy.Prefix.OrderKey;
import com.wavy.redis.RedisService;
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

    @Autowired
    RedisService redisService;

    /**
     * 获取秒杀订单
     * @param userId
     * @param goodsId
     * @return
     */
    public SeckillOrder getSeckillOrderByUserIdGoodsId(long userId,long goodsId){
        //return orderDao.getSeckillOrderByUserIdGoodsId(userId,goodsId);
        // ---- 优化 ----
        // 判断是否秒杀到商品时，不直接查数据库，而是查缓存
        return redisService.get(OrderKey.orderToken,""+userId+"_"+goodsId,SeckillOrder.class);
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

        orderDao.insertOrder(orderInfo);
        //创建秒杀订单记录
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(user.getId());
        orderDao.insertSeckillOrder(seckillOrder);

        // ---- 优化 ----
        // 同时将订单信息写入缓存
        redisService.set(OrderKey.orderToken,""+user.getId()+"_"+goods.getId(),seckillOrder);

        return orderInfo;
    }

    /**
     * 获取订单信息
     * @param orderId
     * @return
     */
    public OrderInfo getOrderById(long orderId){
        return orderDao.getOrderById(orderId);
    }
}
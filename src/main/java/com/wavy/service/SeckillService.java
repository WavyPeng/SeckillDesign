package com.wavy.service;

import com.wavy.Prefix.SeckillKey;
import com.wavy.entity.OrderInfo;
import com.wavy.entity.SeckillOrder;
import com.wavy.entity.User;
import com.wavy.Prefix.GoodsKey;
import com.wavy.redis.RedisService;
import com.wavy.utils.MD5Util;
import com.wavy.utils.UUIDUtil;
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

    @Autowired
    RedisService redisService;

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
        boolean success = goodsService.reduceStock(goods);
        if(success){ // 减库存成功了，才生成订单
            //下订单 记录秒杀订单
            return orderService.createOrder(user,goods);
        }else{ // 减库存失败，这里用redis做一个标记
            setGoodsOver(goods.getId());
            return null;
        }
    }

    /**
     * 获取秒杀结果（异步下单）
     * @param userId
     * @param goodsId
     * @return 订单编号
     */
    public long getSeckillResult(long userId,long goodsId){
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(userId,goodsId);
        if(order != null){   //秒杀成功，返回订单编号
            return order.getOrderId();
        }else{
            //获取商品是否被秒杀完的状态
            boolean isOver = getGoodsOver(goodsId);
            if(isOver){
                return -1;  //秒杀完
            }else{
                return 0;   //排队中
            }
        }
    }

    /**
     * 设置商品被秒杀完
     * @param goodsId
     */
    private void setGoodsOver(long goodsId){
        redisService.set(GoodsKey.isGoodsOver,""+goodsId,true);
    }

    /**
     * 商品是否被秒杀完
     * @param goodsId
     * @return
     */
    private boolean getGoodsOver(long goodsId){
        return redisService.exists(GoodsKey.isGoodsOver,""+goodsId);
    }

    /**
     * 生成秒杀路径
     * @param user
     * @param goodsId
     * @return path
     */
    public String createSeckillPath(User user,long goodsId){
        if(user == null || goodsId<=0){
            return null;
        }
        String path = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(SeckillKey.getSeckillPath,""+user.getId()+"_"+goodsId,path);
        return path;
    }

    /**
     * 校验秒杀路径
     * @param path
     * @param user
     * @param goodsId
     * @return
     */
    public boolean checkPath(String path,User user,long goodsId){
        if(path == null || user == null){
            return false;
        }
        String oldPath = redisService.get(SeckillKey.getSeckillPath,""+user.getId()+"_"+goodsId,String.class);
        return path.equals(oldPath);
    }
}
package com.wavy.service;

import com.wavy.Prefix.SeckillKey;
import com.wavy.entity.OrderInfo;
import com.wavy.entity.SeckillOrder;
import com.wavy.entity.User;
import com.wavy.Prefix.GoodsKey;
import com.wavy.redis.RedisService;
import com.wavy.utils.MD5Util;
import com.wavy.utils.UUIDUtil;
import com.wavy.utils.VerifyCodeUtil;
import com.wavy.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

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


    /**
     * 生成数学公式验证码
     * @param user
     * @param goodsId
     * @return
     */
    public BufferedImage createVerifyCode(User user,long goodsId){
        if(user == null || goodsId<=0){
            return null;
        }
        int width = 80;   // 宽
        int height = 32;  // 高
        // 创建图像
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // 设置背景颜色
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // 绘制边
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // 随机对象
        Random rdm = new Random();
        // 制造噪点
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // 生成验证码
        String verifyCode = VerifyCodeUtil.generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        // 把验证码存到redis中
        int rnd = VerifyCodeUtil.calculate(verifyCode);
        redisService.set(SeckillKey.getSeckillVerifyCode, user.getId()+"_"+goodsId, rnd);
        // 返回验证码
        return image;
    }

    /**
     * 校验验证码
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    public boolean checkVerifyCode(User user,long goodsId,int verifyCode){
        if(user == null || goodsId<=0){
            return false;
        }
        Integer oldVerifyCode = redisService.get(SeckillKey.getSeckillVerifyCode,user.getId()+"_"+goodsId,Integer.class);
        if(oldVerifyCode == null || verifyCode!= oldVerifyCode){
            return false;
        }
        redisService.delete(SeckillKey.getSeckillVerifyCode,user.getId()+"_"+goodsId);
        return true;
    }
}
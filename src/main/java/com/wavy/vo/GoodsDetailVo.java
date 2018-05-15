package com.wavy.vo;

import com.wavy.entity.User;

/**
 * 商品参数
 * Created by WavyPeng on 2018/5/14.
 */
public class GoodsDetailVo {

    // 商品信息
    private GoodsVo goods;
    // 用户信息
    private User user;
    // 秒杀状态
    private int seckill_status;
    // 距离秒杀开始时间
    private int remain_time;

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getSeckill_status() {
        return seckill_status;
    }

    public void setSeckill_status(int seckill_status) {
        this.seckill_status = seckill_status;
    }

    public int getRemain_time() {
        return remain_time;
    }

    public void setRemain_time(int remain_time) {
        this.remain_time = remain_time;
    }
}
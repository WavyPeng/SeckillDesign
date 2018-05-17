package com.wavy.message;

import com.wavy.entity.User;

/**
 * 秒杀信息
 * 记录哪个用户想要秒杀哪个商品
 * Created by WavyPeng on 2018/5/17.
 */
public class SeckillMessage {
    private User user;
    private long goodsId;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
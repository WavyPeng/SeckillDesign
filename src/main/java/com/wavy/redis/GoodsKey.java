package com.wavy.redis;

/**
 * GoodsKey
 * Created by WavyPeng on 2018/5/15.
 */
public class GoodsKey extends BasePrefix{

    private GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsKey getGoodsList = new GoodsKey(60, "goods_list_");
    public static GoodsKey getGoodsDetail = new GoodsKey(60, "goods_detail_");
}
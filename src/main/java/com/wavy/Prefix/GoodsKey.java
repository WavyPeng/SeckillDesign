package com.wavy.Prefix;

/**
 * GoodsKey
 * Created by WavyPeng on 2018/5/15.
 */
public class GoodsKey extends BasePrefix{

    public GoodsKey(String prefix) {
        super(prefix);
    }

    private GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsKey getGoodsList = new GoodsKey(60, "goods_list_");
    public static GoodsKey getGoodsDetail = new GoodsKey(60, "goods_detail_");
    public static GoodsKey getSeckillStock = new GoodsKey(0,"seckill_goods_stock_");
    public static GoodsKey isGoodsOver = new GoodsKey("goods_over");
}
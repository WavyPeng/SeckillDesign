package com.wavy.Prefix;
/**
 * SeckillKey
 * Created by WavyPeng on 2018/5/18.
 */
public class SeckillKey extends BasePrefix{

    public SeckillKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static SeckillKey getSeckillPath = new SeckillKey(60,"seckill_path_");
}
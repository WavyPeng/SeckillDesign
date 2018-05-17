package com.wavy.Prefix;

/**
 * KeyPrefix
 * 区别redis中的key
 * Created by WavyPeng on 2018/5/10.
 */
public interface KeyPrefix {
    /**定义过期时间*/
    public int expireSeconds();
    /**获取前缀*/
    public String getPrefix();
}
package com.wavy.redis;
/**
 * OrderKey
 * Created by WavyPeng on 2018/5/16.
 */
public class OrderKey extends BasePrefix{

    public OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey orderToken = new OrderKey("order_");
}
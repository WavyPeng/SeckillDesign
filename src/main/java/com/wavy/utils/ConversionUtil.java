package com.wavy.utils;

import com.alibaba.fastjson.JSON;

/**
 * 类型转换工具类
 * Created by WavyPeng on 2018/5/17.
 */
public class ConversionUtil {
    /**
     * 将字符串对象转换成Bean对象
     * @param str
     * @return
     */
    @SuppressWarnings("unchecked")
    public static  <T> T stringToBean(String str,Class<T> clazz){
        if(str == null || str.length()<=0 || clazz == null)
            return null;
        if(clazz == int.class || clazz == Integer.class)
            return (T)Integer.valueOf(str);
        else if(clazz == String.class)
            return (T)str;
        else if(clazz == long.class || clazz == Long.class)
            return (T)Long.valueOf(str);
        else
            return JSON.toJavaObject(JSON.parseObject(str),clazz);
    }

    /**
     * 将Bean对象转换成字符串对象
     * @param value
     * @param <T>
     * @return
     */
    public static  <T> String beanToString(T value){
        if(value == null)//如果为空
            return null;
        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz == Integer.class){
            return ""+value;
        }else if(clazz == String.class){
            return (String)value;
        }else if(clazz == long.class || clazz == Long.class){
            return ""+value;
        }else{
            return JSON.toJSONString(value);
        }
    }
}
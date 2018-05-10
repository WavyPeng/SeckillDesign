package com.wavy.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis服务类
 * Created by WavyPeng on 2018/5/09.
 */
@Service
public class RedisService {
    @Autowired
    JedisPool jedisPool;

    /**
     * 返回到连接池
     * @param jedis
     */
    private void returnToPool(Jedis jedis){
        if(jedis != null){
            jedis.close();
        }
    }

    /**
     * 将字符串对象转换成Bean对象
     * @param str
     * @return
     */
    private <T> T stringToBean(String str,Class<T> clazz){
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
    private <T> String beanToString(T value){
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

    /**
     * 获取单个对象
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(String key,Class<T> clazz){
        Jedis jedis = null;
        try{
            //通过JedisPool资源池管理Jedis连接
            jedis = jedisPool.getResource();
            String str = jedis.get(key);
            //将字符串转换成Bean对象
            T t = stringToBean(str,clazz);
            return t;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 设置对象
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> boolean set(String key,T value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            //将Bean对象转换成字符串对象
            String str = beanToString(value);
            jedis.set(key,str);
            return true;
        }finally {
            returnToPool(jedis);
        }
    }
}
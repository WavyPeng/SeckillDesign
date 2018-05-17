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
     * 关闭jedis
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

    /**
     * 获取单个对象
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix prefix,String key,Class<T> clazz){
        Jedis jedis = null;
        try {
            //通过JedisPool资源池管理Jedis连接
            jedis =  jedisPool.getResource();
            //生成真正的key
            String realKey  = prefix.getPrefix() + key;
            String  str = jedis.get(realKey);
            //将字符串转换成Bean对象
            T t =  stringToBean(str, clazz);
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
    public <T> boolean set(KeyPrefix prefix,String key,T value){
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            //将Bean对象转换成字符串对象
            String str = beanToString(value);
            if(str == null || str.length() <= 0) {
                return false;
            }
            //生成真正的key
            String realKey  = prefix.getPrefix() + key;
            int seconds =  prefix.expireSeconds();  //获取过期时间
            if(seconds <= 0) { //key不过期
                jedis.set(realKey, str);
            }else {            //设置key的过期时间为seconds秒
                jedis.setex(realKey, seconds, str);
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 判断key是否存在
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> boolean exists(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 将key中储存的数字值增1
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            //将realKey中存储的数字值增1
            //如果realKey不存在，那么realKey的值会先被初始化为0 ，然后再执行incr操作
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 将key中储存的数字值减1
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long decr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 将key中存储的内容从redis中删除
     * @param prefix
     * @param key
     * @return
     */
    public boolean delete(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            //生成真正的key
            String realKey  = prefix.getPrefix() + key;
            long ret =  jedis.del(realKey);
            return ret > 0;
        }finally {
            returnToPool(jedis);
        }
    }


}
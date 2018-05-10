package com.wavy.redis;

/**
 * KeyPrefix实现类
 * Created by WavyPeng on 2018/5/10.
 */
public abstract class BasePrefix implements KeyPrefix {

    //定义过期时间
    private int expireSeconds;
    //前缀
    private String prefix;

    /**
     * 构造函数
     * 0代表永不过期
     * @param prefix
     */
    public BasePrefix(String prefix) {
        this(0,prefix);
    }

    /**
     * 构造函数
     * @param expireSeconds
     * @param prefix
     */
    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    /**
     * 获取过期时间
     * 默认0代表永不过期
     * @return
     */
    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    /**
     * 获取前缀
     * @return
     */
    @Override
    public String getPrefix() {
        //获取当前类的类名，用于区分prefix
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }
}
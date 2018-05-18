package com.wavy.Prefix;

/**
 * AccessKey 访问控制前缀
 * Created by WavyPeng on 2018/5/18.
 */
public class AccessKey extends BasePrefix{

    public AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static AccessKey accessControl = new AccessKey(5,"access_control_");

    public static AccessKey withExpire(int expireSeconds) {
        return new AccessKey(expireSeconds, "access_control_");
    }
}
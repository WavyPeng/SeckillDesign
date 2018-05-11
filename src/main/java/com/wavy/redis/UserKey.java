package com.wavy.redis;

/**
 * UserKey
 * Created by WavyPeng on 2018/5/10.
 */
public class UserKey extends BasePrefix{

    public static final int TOKEN_EXPIRE = 3600*24*2;

    private UserKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }

    public static UserKey token = new UserKey(TOKEN_EXPIRE,"token_");
}
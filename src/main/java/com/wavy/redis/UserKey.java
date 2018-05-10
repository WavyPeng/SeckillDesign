package com.wavy.redis;

/**
 * UserKey
 * Created by WavyPeng on 2018/5/10.
 */
public class UserKey extends BasePrefix{

    private UserKey(String prefix) {
        super(prefix);
    }

    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");
}
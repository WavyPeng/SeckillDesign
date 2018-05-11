package com.wavy.utils;

import java.util.UUID;

/**
 * UUID工具类
 * Created by WavyPeng on 2018/5/11.
 */
public class UUIDUtil {

    /**
     * 生成UUID
     * @return
     */
    public static String uuid(){
        //将原生UUID的'-'去掉
        return UUID.randomUUID().toString().replace("-","");
    }
}
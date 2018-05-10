package com.wavy.utils;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5加密
 * Created by WavyPeng on 2018/5/10.
 */
public class MD5Util {

    //定义salt值
    private static final String SALT = "w2a0v1y8";

    private static String md5(String str){
        return DigestUtils.md5Hex(str);
    }

    public static String inputPassToFormPass(String inputPass){
        return md5(inputPass+SALT);
    }

    public static String formPassToDbPass(String formPass,String db_salt){
        return md5(formPass+db_salt);
    }

    /**
     * 两次MD5加密
     * @param inputPass
     * @param db_salt
     * @return
     */
    public static String inputPassToDbPass(String inputPass,String db_salt){
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDbPass(formPass,db_salt);
        return dbPass;
    }
}
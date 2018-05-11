package com.wavy.utils;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5加密工具类
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

//    public static void main(String[] args) {
//        System.out.println(inputPassToFormPass("123456"));//cf6e08d61b0350436fbb90e5624320f4
//		System.out.println(formPassToDbPass(inputPassToFormPass("123456"), "w2a0v1y8"));
//		System.out.println(inputPassToDbPass("123456", "w2a0v1y8"));//ff9d15fc030724a1d5290e52ec26aa88
//    }
}
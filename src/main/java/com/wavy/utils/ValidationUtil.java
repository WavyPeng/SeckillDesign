package com.wavy.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 相关校验
 * Created by WavyPeng on 2018/5/10.
 */
public class ValidationUtil {

    //手机号码正则表达式
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\\\d{8}$");

    /**
     * 校验手机号码
     * @param str
     * @return
     */
    public static boolean isMobile(String str){
        //检查是否为空
        if(StringUtils.isEmpty(str))
            return false;
        Matcher m = MOBILE_PATTERN.matcher(str);
        return m.matches();
    }
}
package com.wavy.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验工具类
 * Created by WavyPeng on 2018/5/10.
 */
@Slf4j
public class ValidationUtil {

    //手机号码正则表达式
    private static final Pattern MOBILE_PATTERN = Pattern.compile("1\\d{10}");

    /**
     * 校验手机号码
     * @param str
     * @return
     */
    public static boolean isMobile(String str){
//        log.info("mobile:{}",str);
        //检查是否为空
        if(StringUtils.isEmpty(str))
            return false;
        Matcher m = MOBILE_PATTERN.matcher(str);
//        log.info("status:{}",m.matches());
        return m.matches();
    }
}
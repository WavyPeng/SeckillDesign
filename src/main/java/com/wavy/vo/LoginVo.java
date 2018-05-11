package com.wavy.vo;

import com.wavy.validator.IsMobile;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 登录参数
 * Created by WavyPeng on 2018/5/11.
 */
public class LoginVo {

    @NotNull
    @IsMobile //自定义校验器
    private String mobile;  //手机号码

    @NotNull
    @Length(min=32)
    private String password; //密码

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginVo{" +
                "mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
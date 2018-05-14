package com.wavy.controller;

import com.wavy.Result.Result;
import com.wavy.service.UserService;
import com.wavy.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Created by WavyPeng on 2018/5/10.
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class LoginController {
    @Autowired
    UserService userService;

    /**
     * 跳转到登录界面
     * @return
     */
    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }

    /**
     * 登录
     * @return
     */
    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response,@Valid LoginVo loginVo){
        // 登录
        String token = userService.login(response,loginVo);

        // 异常交给全局异常处理器进行处理
        // 因此这里成功将直接返回
        return Result.success(token);
    }
}
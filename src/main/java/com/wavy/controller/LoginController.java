package com.wavy.controller;

import com.wavy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by WavyPeng on 2018/5/10.
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class LoginController {
    @Autowired
    UserService userService;

    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }


}
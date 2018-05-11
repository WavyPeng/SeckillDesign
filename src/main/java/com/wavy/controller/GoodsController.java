package com.wavy.controller;

import com.wavy.entity.User;
import com.wavy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by WavyPeng on 2018/5/11.
 */
@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

    @Autowired
    UserService userService;

    @RequestMapping("/to_list")
    public String list(Model model,User user){
        log.info("user:{}",user);
        model.addAttribute("user",user);
        return "goods-list";
    }
}
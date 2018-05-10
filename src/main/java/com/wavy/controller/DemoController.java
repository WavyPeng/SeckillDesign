package com.wavy.controller;

import com.wavy.entity.User;
import com.wavy.redis.RedisService;
import com.wavy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name","wavy");
        return "hello";
    }

    @RequestMapping("/db/get")
    public String getById(Model model){
        User user = userService.getById(1);
        model.addAttribute("name",user.getName());
        return "hello";
    }

    @RequestMapping("/redis/get")
    public String redisGet(Model model){
        Long v1 = redisService.get("key1",Long.class);
        model.addAttribute("name",v1);
        return "hello";
    }
}
package com.wavy.controller;

import com.wavy.entity.User;
import com.wavy.redis.GoodsKey;
import com.wavy.redis.RedisService;
import com.wavy.service.GoodsService;
import com.wavy.service.UserService;
import com.wavy.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by WavyPeng on 2018/5/11.
 */
@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

    @Autowired
    UserService userService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    RedisService redisService;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;


    /**
     * 商品列表
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String list(HttpServletResponse response, HttpServletRequest request,
                       Model model, User user){
        model.addAttribute("user",user);

        //---- 添加页面缓存 ----
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsList,"",String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        //查询商品列表
        List<GoodsVo> goodsList = goodsService.getGoodsVoList();
        model.addAttribute("goodsList",goodsList);
//        return "goods-list";

        //---- 添加手动渲染 ----
        //手动渲染
        WebContext webContext = new WebContext(request,response,request.getServletContext(),
                request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods-list",webContext);
        if(!StringUtils.isEmpty(html)){
            //先存入缓存
            redisService.set(GoodsKey.getGoodsList,"",html);
        }
        return html;
    }

    @RequestMapping(value = "/to_detail/{goodsId}",produces = "text/html")
    @ResponseBody
    public String detail(HttpServletRequest request,HttpServletResponse response,
                         Model model, User user,
                         @PathVariable("goodsId")long goodsId){
        model.addAttribute("user",user);

        //---- 添加页面缓存 ----
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail,""+goodsId,String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        //获取商品详细信息
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goods);

        //判断商品是否处于秒杀阶段
        long startTime = goods.getStartDate().getTime();
        long endTime = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        //秒杀状态
        int seckill_status = 0;
        //距离秒杀开始的时间
        int remain_time = 0;

        if(now < startTime){        //秒杀还未开始，倒计时进行中
            seckill_status = 0;
            remain_time = (int)((startTime - now)/1000);
        }else if(now > endTime){  //秒杀结束
            seckill_status = 2;
            remain_time = -1;
            log.info("status:{}","进行中");
        }else {                     //秒杀进行中
            seckill_status = 1;
            remain_time = 0;
        }

        model.addAttribute("seckill_status",seckill_status);
        model.addAttribute("remain_time",remain_time);
//        return "goods-detail";

        //---- 添加手动渲染 ----
        WebContext webContext = new WebContext(request,response,request.getServletContext(),
                request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods-detail",webContext);
        if(!StringUtils.isEmpty(html)){
            //先存入缓存
            redisService.set(GoodsKey.getGoodsDetail,""+goodsId,html);
        }
        return html;
    }
}
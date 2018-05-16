package com.wavy.controller;

import com.sun.org.apache.bcel.internal.classfile.Code;
import com.wavy.Result.CodeMsg;
import com.wavy.Result.Result;
import com.wavy.entity.OrderInfo;
import com.wavy.entity.SeckillOrder;
import com.wavy.entity.User;
import com.wavy.service.GoodsService;
import com.wavy.service.OrderService;
import com.wavy.service.SeckillService;
import com.wavy.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by WavyPeng on 2018/5/12.
 */
@Controller
@RequestMapping("/goods")
@Slf4j
public class SeckillController {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    /**
     * 秒杀
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/do_seckill")
    public String doSeckill(Model model, User user,
                            @RequestParam("goodsId")long goodsId){
        model.addAttribute("user",user);
        //未登录返回登录界面
        if(user == null)
            return "login";

        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();  //注意：这里获取的是秒杀库存

        //没有库存
        if(stock<=0){
            model.addAttribute("errmsg", CodeMsg.SECKILL_OVER.getMsg());
            return "seckill-fail";
        }

        //判断是否重复秒杀
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goodsId);
        if(seckillOrder != null){
            model.addAttribute("errmsg",CodeMsg.SECKILL_REPEAT.getMsg());
            return "seckill-fail";
        }

        //减库存 下订单 记录秒杀订单
        OrderInfo orderInfo = seckillService.seckill(user,goods);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goods);

        return "order-detail";
    }

    @RequestMapping(value = "/static/do_seckill",method = RequestMethod.POST)
    @ResponseBody
    public Result<OrderInfo> staticForDoSeckill(Model model, User user,
                                       @RequestParam("goodsId")long goodsId){
        model.addAttribute("user",user);
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();  //注意：这里获取的是秒杀库存

        //没有库存
        if(stock<=0){
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        //判断是否重复秒杀
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goodsId);
        if(seckillOrder != null){
            return Result.error(CodeMsg.SECKILL_REPEAT);
        }

        //减库存 下订单 记录秒杀订单
        OrderInfo orderInfo = seckillService.seckill(user,goods);
        return Result.success(orderInfo);
    }
}
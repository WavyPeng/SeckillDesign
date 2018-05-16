package com.wavy.controller;

import com.wavy.Result.CodeMsg;
import com.wavy.Result.Result;
import com.wavy.entity.OrderInfo;
import com.wavy.entity.User;
import com.wavy.service.GoodsService;
import com.wavy.service.OrderService;
import com.wavy.service.UserService;
import com.wavy.vo.GoodsVo;
import com.wavy.vo.OrderDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by WavyPeng on 2018/5/15.
 */
@Controller
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    UserService userService;
    @Autowired
    OrderService orderService;
    @Autowired
    GoodsService goodsService;

    /**
     * 订单详情
     * @param model
     * @param user
     * @param orderId
     * @return
     */
    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, User user,
                                      @RequestParam("orderId") long orderId){
        // 用户未登录
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        // 获取订单详情
        OrderInfo order = orderService.getOrderById(orderId);
        if(order == null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }

        // 获取商品详细信息
        long goodsId = order.getId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        OrderDetailVo detail = new OrderDetailVo();
        detail.setGoods(goods);
        detail.setOrder(order);

        return Result.success(detail);
    }
}
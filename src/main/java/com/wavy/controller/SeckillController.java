package com.wavy.controller;

import com.wavy.Result.CodeMsg;
import com.wavy.Result.Result;
import com.wavy.annotation.AccessLimit;
import com.wavy.entity.OrderInfo;
import com.wavy.entity.SeckillOrder;
import com.wavy.entity.User;
import com.wavy.message.SeckillMessage;
import com.wavy.rabbitmq.MQSender;
import com.wavy.Prefix.GoodsKey;
import com.wavy.redis.RedisService;
import com.wavy.service.GoodsService;
import com.wavy.service.OrderService;
import com.wavy.service.SeckillService;
import com.wavy.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by WavyPeng on 2018/5/12.
 */
@Controller
@RequestMapping("/goods")
@Slf4j
public class SeckillController implements InitializingBean{

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    // 内存标记，减少redis访问
    private Map<Long,Boolean> localOverMap = new HashMap<Long,Boolean>();

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

    /**
     * 页面静态化处理秒杀
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
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

    /**
     * RabbitMQ异步处理秒杀
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/rabbitmq/{path}/do_seckill",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> rabbitmqForDoSeckill(Model model, User user,
                                                @RequestParam("goodsId")long goodsId,
                                                @PathVariable("path") String path){
        model.addAttribute("user",user);
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        // ----- 安全优化 -----
        // 判断秒杀路径
        boolean check = seckillService.checkPath(path,user,goodsId);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        // ------ END ------

        // ------ 优化 ------
        // 判断商品秒杀状态
        boolean isOver = localOverMap.get(goodsId);
        if(isOver){  // 说明已经秒杀完毕，直接返回
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        // ------ 内存标记，减少redis访问 ------

        // 预减库存
        long stock = redisService.decr(GoodsKey.getSeckillStock,""+goodsId);
        if(stock < 0){
            localOverMap.put(goodsId,true); // 标记商品已经秒杀完了
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        // 判断是否重复秒杀
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goodsId);
        if(seckillOrder != null){
            return Result.error(CodeMsg.SECKILL_REPEAT);
        }
        // 入队
        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setUser(user);
        seckillMessage.setGoodsId(goodsId);
        sender.sendSeckillMessage(seckillMessage);
        return Result.success(0); //排队中
    }

    /**
     * 异步下单结果
     * @param model
     * @param user
     * @param goodsId
     * @return orderId：订单编号，秒杀成功  -1：秒杀失败  0：排队中
     */
    @RequestMapping(value = "/rabbitmq/seckill_result",method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(Model model, User user,
                                      @RequestParam("goodsId")long goodsId){
        model.addAttribute("user",user);
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        // 判断是否秒杀到商品
        long result = seckillService.getSeckillResult(user.getId(),goodsId);
        return Result.success(result);
    }

    /**
     * 系统初始化
     * 系统启动时就将秒杀商品的库存加载到缓存中
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 查询出所有的商品加载到redis缓存中
        List<GoodsVo> goodsList = goodsService.getGoodsVoList();
        if(goodsList == null){
            return;
        }
        for(GoodsVo goods : goodsList){
            // 将所有秒杀商品的库存存入redis缓存
            redisService.set(GoodsKey.getSeckillStock,""+goods.getId(),goods.getStockCount());

            // 初始化时对商品做标记，表示还未结束
            localOverMap.put(goods.getId(),false);
        }
    }

    /**
     * 生成秒杀路径接口
     * @param user
     * @param goodsId
     * @return
     */
    @AccessLimit(seconds=5, maxCount=5, needLogin=true)
    @RequestMapping(value = "/seckill/path",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillPath(HttpServletRequest request,User user, @RequestParam("goodsId")long goodsId,
                                         @RequestParam(value = "verifyCode",defaultValue = "0")int verifyCode){
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

//        // ----- 限流防刷 -----
//        // 查询访问次数
//        String uri = request.getRequestURI();
//        String key = uri + "_" + user.getId();
//        Integer count = redisService.get(AccessKey.accessControl,key,Integer.class);
//        if(count == null){ // 说明首次访问
//            redisService.set(AccessKey.accessControl,key,1);
//        }else if(count < 5){
//            redisService.incr(AccessKey.accessControl,key);
//        }else{  // 访问超限
//            return Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
//        }
//        // ----- END -----

        // ----- 优化 -----
        // 校验验证码是否正确
        boolean check = seckillService.checkVerifyCode(user,goodsId,verifyCode);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        // ----- END -----

        // 生成秒杀路径
        String path = seckillService.createSeckillPath(user,goodsId);
        return Result.success(path);
    }

    /**
     * 生成数学公式验证码
     * @param response
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/seckill/verifyCode",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getVerifyCodeForSeckill(HttpServletResponse response, User user,
                                                  @RequestParam("goodsId")long goodsId){
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try{
            BufferedImage image  = seckillService.createVerifyCode(user, goodsId);
            // 将图片写入输出流
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(CodeMsg.SECKILL_FAIL);
        }
    }
}
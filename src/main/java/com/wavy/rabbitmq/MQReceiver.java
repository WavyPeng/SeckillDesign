package com.wavy.rabbitmq;

import com.wavy.entity.SeckillOrder;
import com.wavy.entity.User;
import com.wavy.message.SeckillMessage;
import com.wavy.service.GoodsService;
import com.wavy.service.OrderService;
import com.wavy.service.SeckillService;
import com.wavy.utils.ConversionUtil;
import com.wavy.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息接收者
 * Created by WavyPeng on 2018/5/16.
 */
@Service
@Slf4j
public class MQReceiver {

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    SeckillService seckillService;

    /**
     * 对应Direct模式
     * @param message
     */
    // 指定从哪个Queue读数据，这里监听的是MQConfig.QUEUE
    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message){
        log.info("receive message:{}",message);
    }

    /**
     * 对应Topic模式
     * @param message
     */
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE_1)
    public void topicReceive1(String message){
        log.info("receive topic queue1 message:{}",message);
    }
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE_2)
    public void topicReceive2(String message){
        log.info("receive topic queue2 message:{}",message);
    }

    /**
     * 对应Header模式
     * @param message
     */
    @RabbitListener(queues = MQConfig.HEADERS_QUEUE)
    public void headerReceive(byte[] message){
        log.info("header queue receive message:{}",new String(message));
    }

    /**
     * 接收秒杀消息，执行异步下单
     * @param message
     */
    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void seckillReceive(String message){
        log.info("seckill queue receive message:{}",message);

        // 解析接收到的消息
        SeckillMessage seckillMessage = ConversionUtil.stringToBean(message,SeckillMessage.class);
        User user = seckillMessage.getUser();
        long goodsId = seckillMessage.getGoodsId();

        // 获取商品信息 判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        long stock = goods.getStockCount();
        if(stock <= 0){
            return;
        }

        // 判断是否重复秒杀
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goodsId);
        if(null != order){
            return;
        }

        // 减库存 下订单 记录秒杀订单
        seckillService.seckill(user,goods);
    }

}
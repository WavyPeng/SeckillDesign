package com.wavy.rabbitmq;

import com.wavy.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息发送者
 * Created by WavyPeng on 2018/5/16.
 */
@Service
@Slf4j
public class MQSender {
    @Autowired
    AmqpTemplate amqpTemplate ;

    /**
     * 对应Direct模式
     * @param message
     */
    public void send(Object message){
        String msg = RedisService.beanToString(message);
        log.info("send message:{}",msg);
        //指定发送到哪个队列
        amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
    }

    /**
     * 对应Topic模式
     * @param message
     */
    public void sendTopic(Object message){
        String msg = RedisService.beanToString(message);
        log.info("send topic message:{}",msg);
        // queue1和queue2都能收到此消息
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,MQConfig.ROUTING_KEY_1,msg+"1");
        // 只有queue2能收到此消息
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,MQConfig.ROUTING_KEY_2,msg+"2");
    }

    /**
     * 对应Fanout模式
     * @param message
     */
    public void sendFanout(Object message){
        String msg = RedisService.beanToString(message);
        log.info("send fanout message:{}",msg);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg);
    }

    /**
     * 对应Header模式
     * @param message
     */
    public void sendHeader(Object message){
        String msg = RedisService.beanToString(message);
        log.info("send header message:{}",msg);
        MessageProperties properties = new MessageProperties();
        // 指定头部信息
        properties.setHeader("header1","value1");
        properties.setHeader("header2","value2");
        Message obj = new Message(msg.getBytes(),properties);
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE,"",obj);
    }
}
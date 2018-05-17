package com.wavy.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * 消息接收者
 * Created by WavyPeng on 2018/5/16.
 */
@Service
@Slf4j
public class MQReceiver {

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

}
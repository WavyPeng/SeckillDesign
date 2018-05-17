package com.wavy.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * RabbitMQ相关配置
 * Created by WavyPeng on 2018/5/16.
 */
@Configuration
public class MQConfig {

    public static final String QUEUE = "queue";
    public static final String TOPIC_QUEUE_1 = "topic.queue1";
    public static final String TOPIC_QUEUE_2 = "topic.queue2";
    public static final String TOPIC_EXCHANGE = "topicExchange";
    public static final String ROUTING_KEY_1 = "topic.key1";
    // #通配符
    public static final String ROUTING_KEY_2 = "topic.#";
    public static final String FANOUT_EXCHANGE = "fanoutExchange";
    public static final String HEADERS_EXCHANGE = "headerExchange";
    public static final String HEADERS_QUEUE = "header.queue";

    /**
     * Direct模式 交换机Exchange
     * @return
     */
    @Bean
    public Queue queue(){
        return new Queue(QUEUE,true);
    }

    /**
     * Topic模式 交换机Exchange
     * @return
     */
    @Bean
    public Queue topicQueue1(){
        return new Queue(TOPIC_QUEUE_1,true);
    }
    @Bean
    public Queue topicQueue2(){
        return new Queue(TOPIC_QUEUE_2,true);
    }
    // 现将消息放到exchange中，之后exchange再将消息发送给队列
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }
    @Bean
    public Binding topicBinding1(){
        return BindingBuilder.bind(topicQueue1())
                .to(topicExchange())
                .with(ROUTING_KEY_1);
    }
    @Bean
    public Binding topicBinding2(){
        return BindingBuilder.bind(topicQueue2())
                .to(topicExchange())
                .with(ROUTING_KEY_2);
    }

    /**
     * Fanout模式 交换机Exchange
     * @return
     */
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }
    @Bean
    public Binding FanoutBinding1(){
        return BindingBuilder.bind(topicQueue1())
                .to(fanoutExchange());
    }
    @Bean
    public Binding FanoutBinding2(){
        return BindingBuilder.bind(topicQueue2())
                .to(fanoutExchange());
    }

    /**
     * Header模式 交换机Exchange
     * @return
     */
    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(HEADERS_EXCHANGE);
    }
    @Bean
    public Queue headersQueue1(){
        return new Queue(HEADERS_QUEUE,true);
    }
    @Bean
    public Binding headerBinding(){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("header1","value1");
        map.put("header2","value2");
        //只有message的head部分满足map中的<key,value>
        //才会向队列中添加消息
        return BindingBuilder.bind(headersQueue1())
                .to(headersExchange())
                .whereAll(map).match();
    }
}
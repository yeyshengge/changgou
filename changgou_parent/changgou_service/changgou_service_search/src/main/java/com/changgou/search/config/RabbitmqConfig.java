package com.changgou.search.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zps on 2020/1/11 13:41
 */
@Configuration
public class RabbitmqConfig {

    //声明队列
    public static final String AD_UPDATE_QUEUE = "ad_update_queue";
    //声明商品上架队列
    public static final String SEARCH_ADD_QUEUE = "search_add_queue";
    //声明商品下架队列
    public static final String SEARCH_DEL_QUEUE = "search_del_queue";


    //声明队商品上架交换机
    public static final String GOODS_UP_EXCHANGE = "goods_up_exchange";
    //声明队商品下架交换机
    public static final String GOODS_DEL_EXCHANGE = "goods_del_exchange";

    @Bean
    public Queue queue() {
        return new Queue(AD_UPDATE_QUEUE);
    }

    @Bean(SEARCH_ADD_QUEUE)
    public Queue SEARCH_ADD_QUEUE() {
        return new Queue(SEARCH_ADD_QUEUE);
    }

    @Bean(SEARCH_DEL_QUEUE)
    public Queue SEARCH_DEL_QUEUE() {
        return new Queue(SEARCH_DEL_QUEUE);
    }

    //商品上架的队列
    @Bean(GOODS_UP_EXCHANGE)
    public Exchange GOODS_UP_EXCHANGE() {
        return ExchangeBuilder.fanoutExchange(GOODS_UP_EXCHANGE).durable(true).build();
    }

    //商品下架的队列
    @Bean(GOODS_DEL_EXCHANGE)
    public Exchange GOODS_DEL_EXCHANGE() {
        return ExchangeBuilder.fanoutExchange(GOODS_DEL_EXCHANGE).durable(true).build();
    }

    //声明商品上架的交换机
    @Bean
    public Binding GOODS_UP_EXCHANGE_BINDING(@Qualifier(SEARCH_ADD_QUEUE) Queue queue, @Qualifier(GOODS_UP_EXCHANGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }

    //声明商品下架交换机
    @Bean
    public Binding GOODS_DEL_EXCHANGE_BINDING(@Qualifier(SEARCH_DEL_QUEUE) Queue queue, @Qualifier(GOODS_DEL_EXCHANGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }
}

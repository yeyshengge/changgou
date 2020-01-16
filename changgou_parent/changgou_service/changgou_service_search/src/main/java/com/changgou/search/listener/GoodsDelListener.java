package com.changgou.search.listener;

import com.changgou.search.config.RabbitmqConfig;
import com.changgou.search.service.ESManageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zps on 2020/1/12 19:03
 */

@Component
public class GoodsDelListener {

    @Autowired
    private ESManageService esManageService;

    @RabbitListener(queues = RabbitmqConfig.SEARCH_DEL_QUEUE)
    public void receive(String souId) {
        System.out.println("接收到的下架spuId" + souId);

        esManageService.delSkuListBySpuId(souId);

    }
}

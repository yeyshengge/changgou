package com.changgou.search.listener;

import com.changgou.search.config.RabbitmqConfig;
import com.changgou.search.service.ESManageService;
import org.elasticsearch.search.SearchService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zps on 2020/1/12 19:03
 */

@Component
public class GoodsUpListener {

    @Autowired
    private ESManageService esManageService;

    @RabbitListener(queues = RabbitmqConfig.SEARCH_ADD_QUEUE)
    public void receive(String souId) {
        System.out.println("接收到的上架spuId" + souId);
        //执行导入方法
        esManageService.importSkuListBySpuId(souId);


    }
}

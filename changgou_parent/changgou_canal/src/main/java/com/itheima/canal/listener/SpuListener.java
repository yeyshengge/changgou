package com.itheima.canal.listener;

//import com.alibaba.otter.canal.protocol.CanalEntry;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;


@CanalEventListener
public class SpuListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @ListenPoint(schema = "changgou_goods", table = {"tb_spu"}, eventType = CanalEntry.EventType.UPDATE)
    public void spuUp(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        System.err.println("tb_spu表数据发生变化");
        //修改前数据
        Map oldMap = new HashMap<>();
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            oldMap.put(column.getName(), column.getValue());
        }

        //修改后数据
        Map newMap = new HashMap<>();
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            newMap.put(column.getName(), column.getValue());
        }

        System.out.println(oldMap);
        System.out.println(newMap);

        //is_marketable  由0改为1表示上架
        if ("0".equals(oldMap.get("is_marketable")) && "1".equals(newMap.get("is_marketable"))) {
            rabbitTemplate.convertAndSend("goods_up_exchange", "", newMap.get("id")); //发送到mq商品上架交换器上
        }
        //is_marketable  由1改为0表示下架
        if ("1".equals(oldMap.get("is_marketable")) && "0".equals(newMap.get("is_marketable"))) {
            rabbitTemplate.convertAndSend("goods_del_exchange", "", newMap.get("id")); //发送到mq商品上架交换器上
        }
    }
}

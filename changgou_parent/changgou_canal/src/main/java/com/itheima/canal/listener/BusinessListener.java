package com.itheima.canal.listener;


import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author ZJ
 */
@CanalEventListener
public class BusinessListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @ListenPoint(schema = "changgou_business", table = {"tb_ad"})
    public void adUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        System.err.println("广告数据发生变化");
        /*
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        for (CanalEntry.Column column : beforeColumnsList) {
            System.out.println(column.getName()+"-前-"+column.getValue());
        }
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            System.out.println(column.getName()+"-后-"+column.getValue());
        }
        */

        //修改前数据
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            //只需获取position对应的值即可，只需发送位置即可
            if (column.getName().equals("position")) {
                System.out.println("发送消息到mq  ad_update_queue:" + column.getValue());
                rabbitTemplate.convertAndSend("", "ad_update_queue", column.getValue());  //发送消息到mq
                break;
            }
        }

        //修改后数据
        /*for(CanalEntry.Column column: rowData.getAfterColumnsList()) {
            if(column.getName().equals("position")){
                System.out.println("发送消息到mq  ad_update_queue:"+column.getValue());
                rabbitTemplate.convertAndSend("","ad_update_queue",column.getValue());  //发送消息到mq
                break;
            }
        }*/
    }
}

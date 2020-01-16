package com.changgou.goods.listen;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by zps on 2020/1/11 14:39
 */
@Component
public class AdLister {


    @RabbitListener(queues = "ad_update_queue")
    //接收到信息就执行下面这个方法
    public void ad_update(String message) {
        System.out.println("收到信息" + message);
        OkHttpClient okHttpClient = new OkHttpClient();
        //请求该地址会刷新redis里面的数据
        String url = "http://192.168.200.128/ad_update?position=" + message;
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        //执行回调函数
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //发送失败了调用
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //发送成功,输出成功方法
                String body = response.message();
                System.out.println(body);
            }
        });
    }
}

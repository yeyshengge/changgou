package com.changgou.goods;

import com.changgou.utils.IdWorker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by zps on 2020/1/9 19:38
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SnowFlakeTest {

    @Autowired
    private IdWorker idWorker;

    /**
     * 测试雪花算法
     */
    @Test
    public void test01() {
        IdWorker idWorker = new IdWorker(1, 1);
        for (int i = 0; i < 1000; i++) {
            long num = idWorker.nextId();
            System.out.println(num);
        }
    }

    @Test
    public void test02() {
        long l = idWorker.nextId();
        System.out.println(l);
    }
}

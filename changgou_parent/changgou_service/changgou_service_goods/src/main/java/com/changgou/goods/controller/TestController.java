package com.changgou.goods.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.exception.CommonCode;
import com.changgou.exception.ExceptionCast;
import com.changgou.exception.ResultCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zps on 2020/1/12 17:14
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/hello")
    public Result test() {
        int a = 5 / 0;
        return new Result(true, StatusCode.OK, "chenggong");
    }

    @GetMapping("/hello01")
    public Result test01() {
        ExceptionCast.cast(CommonCode.SERVER_ERROR);
        return new Result(true, StatusCode.OK, "chenggong");
    }
}

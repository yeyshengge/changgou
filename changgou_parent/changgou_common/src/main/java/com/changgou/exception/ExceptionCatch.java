package com.changgou.exception;

import com.google.common.collect.ImmutableMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by zps on 2019/12/28 17:01
 */
@ControllerAdvice//增强
public class ExceptionCatch {

    //日志对象
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);
    //定义一个map用来存异常信息
    protected static ImmutableMap<Class<? extends Throwable>, ResultCode> EXCEPTIONS;
    //构建map
    protected static ImmutableMap.Builder<Class<? extends Throwable>, ResultCode> builder = ImmutableMap.builder();

    static {
        //往map中存异常信息
        builder.put(HttpMessageNotReadableException.class, CommonCode.INVALID_PARAM);
        builder.put(ArithmeticException.class, CommonCode.BY_ZERO);
    }

    //捕获CustomException的异常
    @ResponseBody
    @ExceptionHandler(CustomException.class)
    public ResponseResult customException(CustomException e) {
        //打印日志
        LOGGER.error("catch exception", e.getMessage());
        ResultCode resultCode = e.getResultCode();
        return new ResponseResult(resultCode);
    }

    //捕获未知的异常
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseResult Exception(Exception e) {
        //如果集合为空
        if (EXCEPTIONS == null) {
            //通过builder构建map
            EXCEPTIONS = builder.build();
        }
        ResultCode resultCode = EXCEPTIONS.get(HttpMessageNotReadableException.class);
        if (resultCode != null) {
            return new ResponseResult(resultCode);
        }
        //打印日志
        LOGGER.error("catch exception", e.getMessage());
        return new ResponseResult(CommonCode.SERVER_ERROR);
    }

}

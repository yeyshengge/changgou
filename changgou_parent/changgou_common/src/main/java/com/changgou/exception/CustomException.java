package com.changgou.exception;


/**
 * Created by zps on 2019/12/28 16:54
 */
//自定义异常类
public class CustomException extends RuntimeException {
    //定义成员变量
    private ResultCode resultCode;

    public CustomException(ResultCode resultCode) {

        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}

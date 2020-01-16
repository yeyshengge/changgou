package com.changgou.exception;


/**
 * Created by zps on 2019/12/28 16:57
 */
public class ExceptionCast {

    public static void cast(ResultCode resultCode) {
        throw new CustomException(resultCode);
    }

}

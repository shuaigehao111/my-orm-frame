package com.jiahao.exception;

/**
 * 自定义异常
 * @author JiaHao
 */
public class MyException extends RuntimeException {
     private String message;

    public MyException(String message) {
        super("错误信息："+message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

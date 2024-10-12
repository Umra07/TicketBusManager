package com.jfb.lecture5;

public class CustomException extends RuntimeException {

    public CustomException(ExceptionBase e) {
        super(e.getMsg());
    }
}

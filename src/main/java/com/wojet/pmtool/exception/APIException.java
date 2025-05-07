package com.wojet.pmtool.exception;

public class APIException extends RuntimeException{

    public static final long serialVersionUID = 1L;

    public APIException() {
    }

    public APIException(String message) {
        super(message);
    }

}

package com.sonofiroko.email.model.dto;

public class ApiResponse<T> {

    private int code = 200;
    private String message;
    private T body;

    public ApiResponse(String message) {
        this.message = message;
    }

    public ApiResponse(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}

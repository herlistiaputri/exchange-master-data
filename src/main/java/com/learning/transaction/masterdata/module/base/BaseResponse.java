package com.learning.transaction.masterdata.module.base;


import lombok.Data;

@Data
public class BaseResponse<T> {
    private String message;
    private int statusCode;
    private T data;  // Generic type for the response data

    public BaseResponse(String message, int statusCode, T data) {
        this.message = message;
        this.statusCode = statusCode;
        this.data = data;
    }
}

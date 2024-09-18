package com.learning.transaction.masterdata.configuration.exception;

import org.springframework.http.HttpStatus;

public enum RequestResponseEnums {

    ID_NOT_FOUND_EXCEPTION(404, null, "Id Not Found"),
    DUPLICATE_DATA_EXCEPTION(HttpStatus.BAD_REQUEST.value(), null, "Data is already exists");
    private Integer status;
    private String message;
    private String errorMessage;

    RequestResponseEnums(Integer status, String message, String errorMessage) {
        this.status = status;
        this.message = message;
        this.errorMessage = errorMessage;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

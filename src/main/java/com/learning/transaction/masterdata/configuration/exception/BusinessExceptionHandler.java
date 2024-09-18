package com.learning.transaction.masterdata.configuration.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

@ControllerAdvice
public class BusinessExceptionHandler extends RuntimeException{

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> exception(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body( exception.getMessage());
    }

    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<Object> exception(BaseException exception) {
        return ResponseEntity.status(exception.getStatus()).body(exception.getErrorMessage());
    }

//    @ExceptionHandler(value = AccessDeniedException.class)
//    public ResponseEntity<Object> accessDenied(AccessDeniedException exception) {
//        return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(exception.getMessage());
//    }
//
//    @ExceptionHandler({BadCredentialsException.class})
//    @ResponseBody
//    public ResponseEntity<String> badCredentialException(Exception e) {
//        return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                .body(e.getMessage());
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( Objects.requireNonNull(ex.getFieldError()).getDefaultMessage());
    }

}

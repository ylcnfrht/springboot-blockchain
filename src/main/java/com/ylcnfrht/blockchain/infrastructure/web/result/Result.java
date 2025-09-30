package com.ylcnfrht.blockchain.infrastructure.web.result;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

/**
 * Result pattern implementation for API responses.
 * Provides consistent response structure with success/error handling.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {
    
    private final boolean success;
    private final T data;
    private final String message;
    private final String errorCode;
    private final LocalDateTime timestamp;
    
    private Result(boolean success, T data, String message, String errorCode) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }
    
    public static <T> Result<T> success(T data) {
        return new Result<>(true, data, null, null);
    }
    
    public static <T> Result<T> success(T data, String message) {
        return new Result<>(true, data, message, null);
    }
    
    public static <T> Result<T> success(String message) {
        return new Result<>(true, null, message, null);
    }
    
    public static <T> Result<T> error(String message) {
        return new Result<>(false, null, message, null);
    }
    
    public static <T> Result<T> error(String message, String errorCode) {
        return new Result<>(false, null, message, errorCode);
    }
    
    public static <T> Result<T> error(String message, ApiErrorCode errorCode) {
        return new Result<>(false, null, message, errorCode.getCode());
    }
    
    public static <T> Result<T> error(String message, String errorCode, T data) {
        return new Result<>(false, data, message, errorCode);
    }
    
    public static <T> Result<T> error(String message, ApiErrorCode errorCode, T data) {
        return new Result<>(false, data, message, errorCode.getCode());
    }
}

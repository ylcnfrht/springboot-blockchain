package com.ylcnfrht.blockchain.application.exceptions;

/**
 * Base application exception class for all application-level errors.
 * Provides common structure for application exceptions.
 */
public abstract class ApplicationException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final String userMessage;
    
    protected ApplicationException(ErrorCode errorCode, String message, String userMessage) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    protected ApplicationException(ErrorCode errorCode, String message, String userMessage, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public String getUserMessage() {
        return userMessage;
    }
}

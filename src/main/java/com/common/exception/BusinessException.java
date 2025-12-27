package com.common.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class BusinessException extends  RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2457189159617736175L;
	
	private final HttpStatus status;
    private String message;
    private String code;
    private ErrorCodes errorCode;
    private Object[] args;

	/**
     * Arg constructor.
     *
     * @param ex the {@link Throwable}
     * @param message the {@link String} message to set.
     * @param status the {@link HttpStatus} to set.
     */    
    public BusinessException(final Throwable ex, final String message, final HttpStatus status) {
        super(message, ex);
        
        this.status = status;
        this.message = message;
        this.code = "ERROR0000";
    }
    
    /**
     * Arg constructor.
     *
     * @param ex the {@link Throwable}
     * @param message the {@link String} message to set.
     * @param message the {@link String} code to set.
     * @param status the {@link HttpStatus} to set.
     */
    public BusinessException(final Throwable ex, final String message,final String code, final HttpStatus status) {
        super(message, ex);
        this.status = status;
        this.message = message;
        this.code = null;
    }

    /**
     * Arg constructor.
     *
     * @param message the {@link String} message to set.
     * @param status the {@link HttpStatus} to set.
     */
    
    public BusinessException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
        this.message = message;
        this.code = "ERROR0000";
        
    }
    
    public BusinessException(final ErrorCodes errorCode, final HttpStatus status) {
        super();
        this.status = status;
        this.errorCode = errorCode;
        this.message = errorCode.getName();
        this.code = errorCode.getValue();
    }
    
    
    public BusinessException(final ErrorCodes errorCode, Object[] args, final HttpStatus status) {
        super();
        this.status = status;
        this.errorCode = errorCode;
        this.message = errorCode.getName();
        this.code = errorCode.getValue();
        this.args = args;
    }
    
    /**
     * Arg constructor.
     *
     * @param message the {@link String} message to set.
     * @param status the {@link HttpStatus} to set.
     */
    public BusinessException(final String message,final String code, final HttpStatus status) {
    	super(message);
        this.status = status;
        this.message = message;
        this.code = code;
        
    }

    /**
     * Arg constructor.
     *
     * @param ex the {@link Throwable}
     * @param message the {@link String} message to set.
     * @param status the {@link Integer} to set.
     */
    public BusinessException(final Throwable ex, final String message, final Integer status) {
        super(message, ex);
        final HttpStatus resolvedStatus = HttpStatus.resolve(status);
        this.status =  resolvedStatus == null ? HttpStatus.INTERNAL_SERVER_ERROR : resolvedStatus;
        this.message = message;
        this.code = "ERROR0000";
    }

    /**
     * Arg constructor.
     *
     * @param ex the {@link Throwable}
     * @param message the {@link String} message to set.
     */
    public BusinessException(final Throwable ex, final String message) {
        super(message, ex);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = message;
        this.code = "ERROR0000";
    }

    /**
     * Arg constructor.
     *
     * @param message the {@link String} message to set.
     * @param status the {@link HttpStatus} to set.
     */
    public BusinessException(final String message, final Integer status) {
        super(message);
        final HttpStatus resolvedStatus = HttpStatus.resolve(status);
        this.status =  resolvedStatus == null ? HttpStatus.INTERNAL_SERVER_ERROR : resolvedStatus;
        this.message = message;
        this.code = "ERROR0000";
    }

    /**
     * Arg constructor.
     *
     * @param ex the {@link Throwable}
     */
    public BusinessException(final Throwable ex) {
        super(ex);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = ex.getMessage();
        this.code = "ERROR0000";
    }


}

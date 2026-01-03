package org.example.api.common.exception;

import org.example.api.common.model.ResponseCode;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ResponseCode code;

    public BusinessException(ResponseCode code, String logMessage) {
        super(logMessage);
        this.code = code;
    }

    public BusinessException(ResponseCode code, String logMessage, Throwable cause) {
        super(logMessage, cause);
        this.code = code;
    }
}

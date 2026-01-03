package org.example.api.common.exception;

import org.example.api.common.model.ResponseCode;

public class InvalidStateException extends BusinessException {

    public InvalidStateException(String logMessage) {
        super(ResponseCode.CONFLICT, logMessage);
    }

    public InvalidStateException(String logMessage, Throwable cause) {
        super(ResponseCode.CONFLICT, logMessage, cause);
    }
}

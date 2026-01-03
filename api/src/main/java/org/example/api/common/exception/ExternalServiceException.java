package org.example.api.common.exception;

import org.example.api.common.model.ResponseCode;

public class ExternalServiceException extends BusinessException {

    public ExternalServiceException(String message, Throwable cause) {
        super(ResponseCode.EXTERNAL_SERVICE_ERROR, message, cause);
    }
}

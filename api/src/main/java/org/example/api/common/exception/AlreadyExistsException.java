package org.example.api.common.exception;

import org.example.api.common.model.ResponseCode;

public class AlreadyExistsException extends BusinessException {

    public AlreadyExistsException(String message) {
        super(ResponseCode.CONFLICT, message);
    }
}

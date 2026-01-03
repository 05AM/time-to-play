package org.example.api.common.exception;

import org.example.api.common.model.ResponseCode;

public class NotImplementedException extends BusinessException {

    public NotImplementedException() {
        super(ResponseCode.INTERNAL_SERVER_ERROR, "아직 구현되지 않은 기능입니다.");
    }
}

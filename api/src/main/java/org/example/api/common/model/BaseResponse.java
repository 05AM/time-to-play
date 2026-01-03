package org.example.api.common.model;

import lombok.Getter;

@Getter
public class BaseResponse<T> {

    private final String code;
    private final String message;
    private final T data;

    private BaseResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static BaseResponse<Void> of(ResponseCode code) {
        return new BaseResponse<>(code.name(), code.getMessage(), null);
    }

    public static <T> BaseResponse<T> of(ResponseCode code, T data) {
        return new BaseResponse<>(code.name(), code.getMessage(), data);
    }
}


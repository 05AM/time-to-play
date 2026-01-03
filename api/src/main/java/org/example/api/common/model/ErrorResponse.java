package org.example.api.common.model;

import java.util.List;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String code;
    private final String message;
    private final List<FieldErrorDetail> errors;

    private ErrorResponse(String code, String message, List<FieldErrorDetail> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    public static ErrorResponse of(ResponseCode code) {
        return new ErrorResponse(code.name(), code.getMessage(), null);
    }

    public static ErrorResponse of(ResponseCode code, List<FieldErrorDetail> errors) {
        return new ErrorResponse(code.name(), code.getMessage(), errors);
    }

    public record FieldErrorDetail(String field, String reason) {
    }
}

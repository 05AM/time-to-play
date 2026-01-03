package org.example.api.common.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.example.api.common.model.ErrorResponse;
import org.example.api.common.model.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String COMMON_EXCEPTION_FORMAT = "[{}] {}";
    private static final String BUSINESS_EXCEPTION_FORMAT = "Business exception: [{}] ({}) {}";
    private static final String INTERNAL_EXCEPTION_FORMAT = "Unhandled exception: [{}] {}";

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        BindException.class
    })
    public ResponseEntity<ErrorResponse> handleBindingExceptions(BindException e) {
        log.warn(COMMON_EXCEPTION_FORMAT, e.getClass().getSimpleName(), e.getMessage(), e);

        List<ErrorResponse.FieldErrorDetail> errors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new ErrorResponse.FieldErrorDetail(
                error.getField(),
                error.getDefaultMessage()
            ))
            .collect(Collectors.toList());

        return ResponseEntity
            .badRequest()
            .body(ErrorResponse.of(ResponseCode.BAD_REQUEST_ERROR, errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("JSON parse error: {}", e.getMessage());

        return ResponseEntity
            .badRequest()
            .body(ErrorResponse.of(ResponseCode.JSON_PARSE_ERROR));
    }

    /**
     * 비지니스 예외
     */
    @ExceptionHandler({
        ExternalServiceException.class
    })
    public ResponseEntity<ErrorResponse> handleCriticalBusinessExceptions(BusinessException e) {
        log.error(BUSINESS_EXCEPTION_FORMAT, e.getClass().getSimpleName(), e.getCode(), e.getMessage(), e);

        ResponseCode code = e.getCode();
        return ResponseEntity
            .status(code.getHttpStatus())
            .body(ErrorResponse.of(code));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.warn(BUSINESS_EXCEPTION_FORMAT, e.getClass().getSimpleName(), e.getCode(), e.getMessage(), e);

        ResponseCode code = e.getCode();
        return ResponseEntity
            .status(code.getHttpStatus())
            .body(ErrorResponse.of(code));
    }

    /**
     * 서버 내부 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error(INTERNAL_EXCEPTION_FORMAT, e.getClass().getSimpleName(), e.getMessage(), e);

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.of(ResponseCode.INTERNAL_SERVER_ERROR));
    }
}

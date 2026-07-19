package com.example.flowdesk.common.exception;


import com.example.flowdesk.common.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<Void> handleAccessDeniedException(AccessDeniedException ex) {
        return R.failed(
                ErrorCode.ACCESS_DENIED.getCode(),
                ErrorCode.ACCESS_DENIED.getMessage()
        );
    }


    //兜底异常处理
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        return R.failed(
                ErrorCode.SYSTEM_ERROR.getCode(),
                ErrorCode.SYSTEM_ERROR.getMessage()
        );
    }


    @ExceptionHandler(BusinessException.class)
    public R<Void> handleBusinessException (BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return R.failed(errorCode.getCode(), ex.getMessage());
    }


    //参数校验基础
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(ErrorCode.VALIDATION_ERROR.getMessage());

        return R.failed(ErrorCode.VALIDATION_ERROR.getCode(), message);
    }



}

package com.example.flowdesk.common.exception;


import com.example.flowdesk.common.response.R;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    //兜底异常处理
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception ex) {
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

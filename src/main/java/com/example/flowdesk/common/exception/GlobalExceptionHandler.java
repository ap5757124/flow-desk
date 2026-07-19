package com.example.flowdesk.common.exception;


import com.example.flowdesk.common.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;


/**
 * 全局异常处理器。
 *
 * <p>Controller 或 Service 抛出的异常会先到这里，再被转换成统一的
 * {@code R<Void>} 响应。这样每个 Controller 就不必重复 try/catch。</p>
 */
@Slf4j // Lombok 自动生成名为 log 的日志对象
@RestControllerAdvice // 对所有 REST Controller 生效，统一把异常转换为响应体
public class GlobalExceptionHandler {


    /** 处理已登录用户没有目标权限的情况。 */
    @ExceptionHandler(AccessDeniedException.class) // 捕获 Spring Security 的权限不足异常
    public ResponseEntity<R<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(R.failed(
                ErrorCode.ACCESS_DENIED.getCode(),
                ErrorCode.ACCESS_DENIED.getMessage()
        ));
    }


    // 兜底异常处理
    /** 处理没有专门处理器覆盖的异常，并记录完整堆栈供服务端排查。 */
    @ExceptionHandler(Exception.class) // 捕获未被其他方法处理的所有异常
    public ResponseEntity<R<Void>> handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(R.failed(
                ErrorCode.SYSTEM_ERROR.getCode(),
                ErrorCode.SYSTEM_ERROR.getMessage()
        ));
    }


    /** 将项目自己的业务异常映射为对应的 HTTP 状态码。 */
    @ExceptionHandler(BusinessException.class) // 捕获项目主动抛出的业务异常
    public ResponseEntity<R<Void>> handleBusinessException(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        HttpStatus status = HttpStatus.resolve(errorCode.getCode());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(status).body(R.failed(errorCode.getCode(), ex.getMessage()));
    }


    // 参数校验基础
    /** 处理 {@code @Valid} 校验失败，例如登录请求缺少用户名。 */
    @ExceptionHandler(MethodArgumentNotValidException.class) // 捕获 @Valid 触发的字段校验异常
    public ResponseEntity<R<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(ErrorCode.VALIDATION_ERROR.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(R.failed(ErrorCode.VALIDATION_ERROR.getCode(), message));
    }

    /** 处理 JSON 格式错误或请求体无法反序列化的情况。 */
    @ExceptionHandler(HttpMessageNotReadableException.class) // 捕获 JSON 格式错误或类型转换失败
    public ResponseEntity<R<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(R.failed(ErrorCode.VALIDATION_ERROR.getCode(), ErrorCode.VALIDATION_ERROR.getMessage()));
    }



}

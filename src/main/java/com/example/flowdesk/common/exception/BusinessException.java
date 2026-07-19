package com.example.flowdesk.common.exception;


import lombok.Getter;

/**
 * 业务异常。
 *
 * <p>业务代码可以抛出这个异常表达“请求本身格式正确，但业务规则不允许”，
 * 例如密码错误、用户被禁用或租户不匹配。统一异常处理器会把它转换成 HTTP 响应。</p>
 */
@Getter // Lombok 在编译期为 errorCode 生成 getErrorCode() 方法
public class BusinessException extends RuntimeException {

    /** 业务错误码，用于决定 HTTP 状态和响应体中的 code。 */
    private final ErrorCode errorCode;

    /** 使用错误码默认文案创建异常。 */
    public BusinessException(ErrorCode errorCode)  {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /** 使用指定错误码和更具体的提示信息创建异常。 */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}

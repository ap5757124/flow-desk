package com.example.flowdesk.common.exception;


import lombok.Getter;

/**
 * 系统统一错误码。
 *
 * <p>这里的 code 同时作为 API 响应体中的业务码和 HTTP 状态码，
 * 这样客户端不需要从 HTTP 200 中再猜测请求是否失败。</p>
 */
@Getter // Lombok 为 code 和 message 自动生成 getter
public enum ErrorCode {

    SUCCESS(200, "请求成功"),
    VALIDATION_ERROR(400, "请求参数不合法"),
    AUTHENTICATION_FAILED(401, "认证失败"),
    ACCESS_DENIED(403, "无访问权限"),
    RESOURCE_NOT_FOUND(404, "资源不存在"),
    BUSINESS_CONFLICT(409, "业务状态冲突"),
    DUPLICATE_REQUEST(409, "重复操作"),
    SYSTEM_ERROR(500, "系统异常");

    /** 对外返回的数字错误码，同时对应 HTTP 状态码。 */
    private final int code;
    /** 面向客户端的默认错误提示。 */
    private final String message;

    /** 创建一个错误码定义。 */
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}

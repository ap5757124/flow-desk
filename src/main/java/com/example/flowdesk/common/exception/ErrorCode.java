package com.example.flowdesk.common.exception;


import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(200, "请求成功"),
    VALIDATION_ERROR(400, "请求参数不合法"),
    AUTHENTICATION_FAILED(401, "认证失败"),
    ACCESS_DENIED(403, "无访问权限"),
    RESOURCE_NOT_FOUND(404, "资源不存在"),
    SYSTEM_ERROR(500, "系统异常");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

//    public int getCode() {
//        return code;
//    }
//
//    public String getMessage() {
//        return message;
//    }

}

package com.example.flowdesk.common.response;

import com.example.flowdesk.common.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class R<T> implements Serializable {

    private int code;
    private String message;
    private T data;
    private String traceId;
    private LocalDateTime timestamp;

    private R() {
        this.timestamp = LocalDateTime.now();
    }

    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.setCode(ErrorCode.SUCCESS.getCode());
        r.setMessage(ErrorCode.SUCCESS.getMessage());
        r.setData(data);
        return r;
    }

    public static <T> R<T> failed(int code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

}

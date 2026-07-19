package com.example.flowdesk.common.response;

import com.example.flowdesk.common.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * API 的统一响应包装类。
 *
 * @param <T> data 字段实际承载的数据类型
 */
@Data // Lombok 生成字段的 getter、setter、equals、hashCode 和 toString
public class R<T> implements Serializable {

    /** 业务响应码，成功通常为 200，失败时与 HTTP 状态保持一致。 */
    private int code;
    /** 给前端或调用方展示的提示信息。 */
    private String message;
    /** 成功时的业务数据，失败时通常为 null。 */
    private T data;
    /** 链路追踪 ID，接入 tracing 后用于串联日志。 */
    private String traceId;
    /** 服务生成响应的时间。 */
    private LocalDateTime timestamp;

    /** 私有构造方法，统一通过 success/failed 工厂方法创建响应。 */
    private R() {
        this.timestamp = LocalDateTime.now();
    }

    /** 创建一个成功响应。 */
    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.setCode(ErrorCode.SUCCESS.getCode());
        r.setMessage(ErrorCode.SUCCESS.getMessage());
        r.setData(data);
        return r;
    }

    /** 创建一个失败响应。 */
    public static <T> R<T> failed(int code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

}

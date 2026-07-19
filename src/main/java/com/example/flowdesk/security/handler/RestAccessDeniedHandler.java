package com.example.flowdesk.security.handler;

import com.example.flowdesk.common.exception.ErrorCode;
import com.example.flowdesk.common.response.R;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Spring Security 的授权失败处理器。
 *
 * <p>用户已经通过认证但缺少目标权限时返回 HTTP 403。</p>
 */
@Component // 注册为 Spring Bean，供 SecurityConfig 配置到过滤链
@RequiredArgsConstructor // Lombok 生成 ObjectMapper 构造器
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    /** 将统一响应对象写成 JSON。 */
    private final ObjectMapper objectMapper;

    /** 向客户端写出统一的 403 JSON 响应。 */
    @Override // 实现 AccessDeniedHandler 接口约定的方法
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        // 403 表示身份已确认，但当前身份不允许执行该操作。
        response.setStatus(ErrorCode.ACCESS_DENIED.getCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(
                response.getOutputStream(),
                R.failed(ErrorCode.ACCESS_DENIED.getCode(), ErrorCode.ACCESS_DENIED.getMessage())
        );
    }
}

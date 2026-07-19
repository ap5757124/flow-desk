package com.example.flowdesk.security.handler;

import com.example.flowdesk.common.exception.ErrorCode;
import com.example.flowdesk.common.response.R;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Spring Security 的认证失败处理器。
 *
 * <p>当请求未携带有效身份时返回 HTTP 401，并保持项目统一的 R 响应格式。</p>
 */
@Component // 注册为 Spring Bean，供 SecurityConfig 和 JWT 过滤器复用
@RequiredArgsConstructor // Lombok 生成 ObjectMapper 构造器，Spring 自动注入 Jackson
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /** 将 Java 响应对象序列化成 JSON。 */
    private final ObjectMapper objectMapper;

    /** 向客户端写出统一的 401 JSON 响应。 */
    @Override // 实现 AuthenticationEntryPoint 接口约定的方法
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        // 必须同时设置真实 HTTP 状态码和 JSON 响应体中的业务 code。
        response.setStatus(ErrorCode.AUTHENTICATION_FAILED.getCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(
                response.getOutputStream(),
                R.failed(ErrorCode.AUTHENTICATION_FAILED.getCode(), ErrorCode.AUTHENTICATION_FAILED.getMessage())
        );
    }
}

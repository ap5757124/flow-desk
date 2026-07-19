package com.example.flowdesk.security.handler;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

/** 验证 Spring Security 的 401、403 处理器会输出统一 JSON。 */
class SecurityErrorHandlerTest {

    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    /** 未认证请求应返回 HTTP 401 和 code=401。 */
    @Test // 标记为 JUnit 测试方法
    void shouldWriteJsonUnauthorizedResponse() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        RestAuthenticationEntryPoint entryPoint = new RestAuthenticationEntryPoint(objectMapper);

        entryPoint.commence(
                new MockHttpServletRequest(),
                response,
                new BadCredentialsException("invalid token")
        );

        JsonNode body = objectMapper.readTree(response.getContentAsString());
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).startsWith("application/json");
        assertThat(body.get("code").asInt()).isEqualTo(401);
    }

    /** 已认证但无权限的请求应返回 HTTP 403 和 code=403。 */
    @Test // 标记为 JUnit 测试方法
    void shouldWriteJsonForbiddenResponse() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        RestAccessDeniedHandler handler = new RestAccessDeniedHandler(objectMapper);

        handler.handle(
                new MockHttpServletRequest(),
                response,
                new AccessDeniedException("forbidden")
        );

        JsonNode body = objectMapper.readTree(response.getContentAsString());
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentType()).startsWith("application/json");
        assertThat(body.get("code").asInt()).isEqualTo(403);
    }
}

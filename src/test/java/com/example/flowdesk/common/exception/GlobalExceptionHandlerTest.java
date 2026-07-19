package com.example.flowdesk.common.exception;

import com.example.flowdesk.common.response.R;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/** 验证全局异常处理器的 HTTP 状态和业务 code 保持一致。 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    /** 业务认证异常应映射为 HTTP 401。 */
    @Test // 标记为 JUnit 测试方法
    void shouldUseBusinessErrorCodeAsHttpStatus() {
        ResponseEntity<R<Void>> response = handler.handleBusinessException(
                new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "认证失败")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(401);
    }

    /** 未识别异常应被兜底映射为 HTTP 500。 */
    @Test // 标记为 JUnit 测试方法
    void shouldReturnInternalServerErrorForUnhandledException() {
        ResponseEntity<R<Void>> response = handler.handleException(new RuntimeException("failure"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(500);
    }
}

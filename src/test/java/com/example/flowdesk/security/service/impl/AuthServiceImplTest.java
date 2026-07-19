package com.example.flowdesk.security.service.impl;

import com.example.flowdesk.common.exception.BusinessException;
import com.example.flowdesk.common.exception.ErrorCode;
import com.example.flowdesk.security.dto.req.RefreshTokenReq;
import com.example.flowdesk.security.mapstruct.LoginResStructMapper;
import com.example.flowdesk.security.token.JwtTokenProvider;
import com.example.flowdesk.system.entity.SysUser;
import com.example.flowdesk.system.service.SysUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/** 使用 Mockito 隔离数据库和 JWT 组件，单独验证认证业务规则。 */
@ExtendWith(MockitoExtension.class) // 让 JUnit 创建和注入 @Mock、@InjectMocks 对象
class AuthServiceImplTest {

    @Mock // 创建用户服务模拟对象，测试不会访问真实数据库
    private SysUserService sysUserService;

    @Mock // 创建登录响应转换器模拟对象
    private LoginResStructMapper loginResStructMapper;

    @Mock // 创建密码编码器模拟对象
    private PasswordEncoder passwordEncoder;

    @Mock // 创建 JWT 提供者模拟对象，可控制解析成功或抛异常
    private JwtTokenProvider jwtTokenProvider;

    @Mock // 创建 JWT claims 模拟对象
    private Claims claims;

    @InjectMocks // 创建被测 AuthServiceImpl，并把上面的 Mock 注入构造器
    private AuthServiceImpl authService;

    /** 非法 Refresh Token 必须转换为认证失败业务异常。 */
    @Test // 标记为 JUnit 测试方法
    void shouldConvertInvalidRefreshTokenToAuthenticationFailure() {
        RefreshTokenReq request = refreshRequest("invalid-token");
        when(jwtTokenProvider.parseRefreshToken("invalid-token"))
                .thenThrow(new JwtException("invalid token"));

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
                        .isEqualTo(ErrorCode.AUTHENTICATION_FAILED));
    }

    /** Token 租户与数据库用户租户不一致时必须拒绝刷新。 */
    @Test // 标记为 JUnit 测试方法
    void shouldRejectRefreshTokenWhenTenantDoesNotMatchUser() {
        RefreshTokenReq request = refreshRequest("refresh-token");
        SysUser sysUser = new SysUser();
        sysUser.setId(2);
        sysUser.setTenantId(1);
        sysUser.setStatus("1");

        when(jwtTokenProvider.parseRefreshToken("refresh-token")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("2");
        when(claims.get("tenantId", Integer.class)).thenReturn(2);
        when(sysUserService.getById(2)).thenReturn(sysUser);

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
                        .isEqualTo(ErrorCode.AUTHENTICATION_FAILED));
    }

    private RefreshTokenReq refreshRequest(String token) {
        RefreshTokenReq request = new RefreshTokenReq();
        request.setRefreshToken(token);
        return request;
    }
}

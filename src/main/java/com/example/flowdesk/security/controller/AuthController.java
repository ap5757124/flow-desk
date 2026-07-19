package com.example.flowdesk.security.controller;


import com.example.flowdesk.common.response.R;
import com.example.flowdesk.security.dto.req.LoginReq;
import com.example.flowdesk.security.dto.req.RefreshTokenReq;
import com.example.flowdesk.security.dto.res.LoginRes;
import com.example.flowdesk.security.dto.res.RefreshTokenRes;
import com.example.flowdesk.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证相关 HTTP 接口。
 *
 * <p>Controller 只负责接收和校验请求、调用 Service、包装响应，
 * 密码验证和 Token 生成等业务逻辑放在 AuthService 中。</p>
 */
@RequiredArgsConstructor // Lombok 生成包含 authService 的构造器，Spring 通过它注入依赖
@RestController // 声明 REST 控制器，方法返回值会自动序列化为 JSON
@RequestMapping("/auth") // 为本类所有接口添加统一的 /auth 路径前缀
public class AuthController {

    /** 认证用例服务。 */
    private final AuthService authService;

    /** 使用租户、用户名和密码登录，成功后返回两种 Token。 */
    @PostMapping(value = "login", name = "认证登录") // 接收 POST /auth/login 请求
    public R<LoginRes> login(
            @Valid // 在进入方法前执行 LoginReq 字段上的校验规则
            @RequestBody // 将请求 JSON 反序列化为 LoginReq
            LoginReq loginReq
    ) {
        LoginRes loginRes = authService.login(loginReq);
        return R.success(loginRes);
    }

    /** 使用合法的 Refresh Token 换取新的 Token。 */
    @PostMapping(value = "/refresh", name = "刷新token") // 接收 POST /auth/refresh 请求
    public R<RefreshTokenRes> refresh(
            @Valid // 校验 refreshToken 不能为空
            @RequestBody // 将请求 JSON 反序列化为 RefreshTokenReq
            RefreshTokenReq refreshTokenReq
    ) {
        RefreshTokenRes refreshTokenRes = authService.refreshToken(refreshTokenReq);
        return R.success(refreshTokenRes);
    }


}

package com.example.flowdesk.security.service;

import com.example.flowdesk.security.dto.req.LoginReq;
import com.example.flowdesk.security.dto.req.RefreshTokenReq;
import com.example.flowdesk.security.dto.res.LoginRes;
import com.example.flowdesk.security.dto.res.RefreshTokenRes;

/** 认证用例服务接口，定义登录和刷新令牌两个业务入口。 */
public interface AuthService {

    /**
     * 校验租户、用户名、用户状态和密码，成功后签发 Token。
     */
    LoginRes login(LoginReq loginReq);

    /** 校验 Refresh Token 和用户当前状态，成功后签发新 Token。 */
    RefreshTokenRes refreshToken(RefreshTokenReq refreshTokenReq);
}

package com.example.flowdesk.security.service;

import com.example.flowdesk.security.dto.req.LoginReq;
import com.example.flowdesk.security.dto.req.RefreshTokenReq;
import com.example.flowdesk.security.dto.res.LoginRes;
import com.example.flowdesk.security.dto.res.RefreshTokenRes;

public interface AuthService {
    LoginRes login(LoginReq loginReq);

    RefreshTokenRes refreshToken(RefreshTokenReq refreshTokenReq);
}

package com.example.flowdesk.security.service.impl;

import com.example.flowdesk.common.exception.BusinessException;
import com.example.flowdesk.common.exception.ErrorCode;
import com.example.flowdesk.security.dto.req.LoginReq;
import com.example.flowdesk.security.dto.req.RefreshTokenReq;
import com.example.flowdesk.security.dto.res.LoginRes;
import com.example.flowdesk.security.dto.res.RefreshTokenRes;
import com.example.flowdesk.security.mapstruct.LoginResStructMapper;
import com.example.flowdesk.security.service.AuthService;
import com.example.flowdesk.security.token.JwtTokenProvider;
import com.example.flowdesk.system.entity.SysUser;
import com.example.flowdesk.system.service.SysUserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserService sysUserService;

    private final LoginResStructMapper loginResStructMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public LoginRes login(LoginReq loginReq) {

        SysUser sysUser = sysUserService.findByTenantIdAndUsername(
                loginReq.getTenantId(),
                loginReq.getUsername()
        );
        if (sysUser == null) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "用户名租户错误");
        }

        if (sysUser.getStatus().equals("0")) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "用户已经被禁用");
        }

        if (!passwordEncoder.matches(loginReq.getPassword(), sysUser.getPassword())) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "密码错误");
        }

        LoginRes loginRes = loginResStructMapper.toLoginRes(sysUser);
        loginRes.setAccessToken(jwtTokenProvider.generateAccessToken(sysUser));
        loginRes.setRefreshToken(jwtTokenProvider.generateRefreshToken(sysUser));
        return loginRes;
    }

    @Override
    public RefreshTokenRes refreshToken(RefreshTokenReq refreshTokenReq) {
        Claims claims = jwtTokenProvider.parseRefreshToken(refreshTokenReq.getRefreshToken());

        int userId = Integer.parseInt(claims.getSubject());
        SysUser sysUser = sysUserService.getById(userId);

        if (sysUser == null) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "用户不存在");
        }

        if ("0".equals(sysUser.getStatus())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "用户已经被禁用");
        }

        RefreshTokenRes res = new RefreshTokenRes();
        res.setAccessToken(jwtTokenProvider.generateAccessToken(sysUser));
        res.setRefreshToken(jwtTokenProvider.generateRefreshToken(sysUser));
        return res;
    }
}

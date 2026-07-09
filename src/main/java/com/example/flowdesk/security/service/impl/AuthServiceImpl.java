package com.example.flowdesk.security.service.impl;

import com.example.flowdesk.common.exception.BusinessException;
import com.example.flowdesk.common.exception.ErrorCode;
import com.example.flowdesk.security.dto.req.LoginReq;
import com.example.flowdesk.security.dto.res.LoginRes;
import com.example.flowdesk.security.mapstruct.LoginResStructMapper;
import com.example.flowdesk.security.service.AuthService;
import com.example.flowdesk.system.entity.SysUser;
import com.example.flowdesk.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserService sysUserService;

    private final LoginResStructMapper loginResStructMapper;

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

        if (!sysUser.getPassword().equals(loginReq.getPassword())) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "密码错误");
        }

        return loginResStructMapper.toLoginRes(sysUser);
    }
}

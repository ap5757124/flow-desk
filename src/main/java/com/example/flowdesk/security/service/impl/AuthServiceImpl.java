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
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证业务实现。
 *
 * <p>这里负责编排用户查询、状态检查、BCrypt 密码比对和 JWT 签发。
 * Controller 不直接接触密码哈希或 Token 实现细节。</p>
 */
@Service // 注册为业务层 Spring Bean，供 AuthController 注入
@RequiredArgsConstructor // Lombok 为全部 final 依赖生成构造器
public class AuthServiceImpl implements AuthService {

    /** 查询用户及用户当前状态。 */
    private final SysUserService sysUserService;

    /** 将 SysUser 安全地转换为 LoginRes。 */
    private final LoginResStructMapper loginResStructMapper;

    /** 使用 BCrypt 比对明文密码和数据库密码哈希。 */
    private final PasswordEncoder passwordEncoder;

    /** 生成并解析 Access Token、Refresh Token。 */
    private final JwtTokenProvider jwtTokenProvider;


    /** 完成一次登录认证并签发 Token。 */
    @Override // 实现 AuthService 定义的登录用例
    public LoginRes login(LoginReq loginReq) {

        // 用户名只在租户内唯一，因此登录查询必须同时带 tenantId。
        SysUser sysUser = sysUserService.findByTenantIdAndUsername(
                loginReq.getTenantId(),
                loginReq.getUsername()
        );
        if (sysUser == null) {
            // 不区分“租户不存在”和“用户名不存在”，避免向外泄露账号信息。
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "用户名租户错误");
        }

        // 被禁用用户即使密码正确也不能登录。
        if (sysUser.getStatus().equals("0")) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "用户已经被禁用");
        }

        // matches 内部使用数据库哈希中的盐值，不能通过重新 encode 后比较字符串。
        if (!passwordEncoder.matches(loginReq.getPassword(), sysUser.getPassword())) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "密码错误");
        }

        // 先映射非敏感用户字段，再分别生成两种用途不同的 Token。
        LoginRes loginRes = loginResStructMapper.toLoginRes(sysUser);
        loginRes.setAccessToken(jwtTokenProvider.generateAccessToken(sysUser));
        loginRes.setRefreshToken(jwtTokenProvider.generateRefreshToken(sysUser));
        return loginRes;
    }

    /** 校验 Refresh Token，并根据数据库中的最新用户信息重新签发 Token。 */
    @Override // 实现 AuthService 定义的刷新令牌用例
    public RefreshTokenRes refreshToken(RefreshTokenReq refreshTokenReq) {
        Claims claims;
        int userId;
        try {
            // parseRefreshToken 同时校验签名、过期时间和 tokenType=refresh。
            claims = jwtTokenProvider.parseRefreshToken(refreshTokenReq.getRefreshToken());
            userId = Integer.parseInt(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "刷新令牌无效");
        }

        // 重新查询数据库，确保用户删除或禁用后旧 Refresh Token 不能继续使用。
        SysUser sysUser = sysUserService.getById(userId);

        if (sysUser == null) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "用户不存在");
        }

        if ("0".equals(sysUser.getStatus())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "用户已经被禁用");
        }

        // Token 中的租户必须与用户当前租户一致，避免跨租户身份被续签。
        Integer tokenTenantId = claims.get("tenantId", Integer.class);
        if (tokenTenantId == null || tokenTenantId != sysUser.getTenantId()) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "刷新令牌租户不匹配");
        }

        // 返回一组新 Token；后续接入 Redis 时还可以让旧 Refresh Token 立即失效。
        RefreshTokenRes res = new RefreshTokenRes();
        res.setAccessToken(jwtTokenProvider.generateAccessToken(sysUser));
        res.setRefreshToken(jwtTokenProvider.generateRefreshToken(sysUser));
        return res;
    }
}

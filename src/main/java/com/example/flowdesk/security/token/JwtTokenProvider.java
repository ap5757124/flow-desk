package com.example.flowdesk.security.token;

import com.example.flowdesk.security.config.JwtProperties;
import com.example.flowdesk.system.entity.SysUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 令牌提供者，集中管理令牌的生成和解析。
 *
 * <p>Access Token 用于访问业务接口，生命周期较短；Refresh Token 只用于刷新，
 * 生命周期较长。两者通过 tokenType claim 严格区分。</p>
 */
@Component // 注册为 Spring Bean，供认证服务和过滤器注入
@RequiredArgsConstructor // Lombok 生成 JwtProperties 构造器
public class JwtTokenProvider {

    /** JWT 密钥和有效期配置。 */
    private final JwtProperties jwtProperties;

    /**
     * 为指定用户生成 Access Token。
     * Token 中只保存建立请求身份所需的最小字段，不保存权限列表。
     */
    public String generateAccessToken(SysUser sysUser) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getAccessTokenExpirationMinutes() * 60 * 1000);

        // subject 是 JWT 约定的主体字段，这里存用户主键 ID。
        return Jwts.builder()
                .subject(String.valueOf(sysUser.getId()))
                .claim("tenantId", sysUser.getTenantId())
                .claim("username", sysUser.getUsername())
                .claim("departmentId", sysUser.getDepartmentId())
                .claim("tokenType", "access")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSecretKey())
                .compact();
    }

    /** 将配置中的字符串密钥转换为 HMAC 签名使用的 SecretKey。 */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /** 解析并确认这是 Access Token，而不是 Refresh Token。 */
    public Claims parseAccessToken(String token) {
        Claims claims = parseToken(token);

        if (!"access".equals(claims.get("tokenType", String.class))) {
            throw new JwtException("Invalid access token");
        }

        return claims;
    }

    /** 解析并确认这是 Refresh Token，而不是 Access Token。 */
    public Claims parseRefreshToken(String token) {
        Claims claims = parseToken(token);

        if (!"refresh".equals(claims.get("tokenType", String.class))) {
            throw new JwtException("Invalid refresh token");
        }

        return claims;
    }

    /**
     * 验证 JWT 签名和过期时间，并返回载荷 claims。
     * 校验失败时 JJWT 会抛出 JwtException。
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** 为指定用户生成 Refresh Token。 */
    public String generateRefreshToken(SysUser sysUser) {
        Date now = new Date();
        Date expiration = new Date(
                now.getTime() + jwtProperties.getRefreshTokenExpirationDays() * 24 * 60 * 60 * 1000
        );

        return Jwts.builder()
                .subject(String.valueOf(sysUser.getId()))
                .claim("tenantId", sysUser.getTenantId())
                .claim("username", sysUser.getUsername())
                .claim("tokenType", "refresh")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSecretKey())
                .compact();
    }
}

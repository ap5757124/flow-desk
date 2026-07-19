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

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtProperties jwtProperties;

    public String generateAccessToken(SysUser sysUser) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getAccessTokenExpirationMinutes() * 60 * 1000);

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

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public Claims parseAccessToken(String token) {
        Claims claims = parseToken(token);

        if (!"access".equals(claims.get("tokenType", String.class))) {
            throw new JwtException("Invalid access token");
        }

        return claims;
    }

    public Claims parseRefreshToken(String token) {
        Claims claims = parseToken(token);

        if (!"refresh".equals(claims.get("tokenType", String.class))) {
            throw new JwtException("Invalid refresh token");
        }

        return claims;
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


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

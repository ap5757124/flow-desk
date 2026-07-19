package com.example.flowdesk.security.filter;

import com.example.flowdesk.security.context.LoginUser;
import com.example.flowdesk.security.token.JwtTokenProvider;
import com.example.flowdesk.system.service.SysPermissionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 认证过滤器，每个 HTTP 请求最多执行一次。
 *
 * <p>主要流程：读取 Authorization 请求头、验证 Access Token、查询实时权限、
 * 创建 Authentication 并写入 SecurityContext。Controller 执行时就能取得登录用户。</p>
 */
@Component // 注册为 Spring Bean，供 SecurityConfig 加入安全过滤链
@RequiredArgsConstructor // Lombok 为 final 依赖生成构造器，Spring 通过构造器注入
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** 负责 JWT 的签发、签名校验和 claims 解析。 */
    private final JwtTokenProvider jwtTokenProvider;

    /** 根据用户和租户实时查询有效权限码。 */
    private final SysPermissionService sysPermissionService;

    /** Token 无效时使用统一的 401 JSON 响应。 */
    private final AuthenticationEntryPoint authenticationEntryPoint;

    /**
     * 对当前请求执行 JWT 认证。
     *
     * @param request 当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param filterChain 后续过滤器链，认证完成后必须继续调用
     */
    @Override // 重写 OncePerRequestFilter 定义的核心过滤方法
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 标准格式是 Authorization: Bearer <accessToken>。
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            // 没有 Token 时不在这里直接报错，让后续授权规则判断接口是否允许匿名访问。
            filterChain.doFilter(request, response);
            return;
        }

        // 去掉 "Bearer " 前缀，只保留 JWT 字符串。
        String token = authorization.substring(7);

        try {
            // 校验签名、过期时间，并确认 tokenType 必须是 access。
            Claims claims = jwtTokenProvider.parseAccessToken(token);

            // subject 保存用户 ID，其他身份字段保存在自定义 claims 中。
            int userId = Integer.parseInt(claims.getSubject());
            int tenantId = claims.get("tenantId", Integer.class);
            String username = claims.get("username", String.class);
            int departmentId = claims.get("departmentId", Integer.class);

            // 权限不直接写死在 Token 中，每次请求查询数据库，禁用角色/权限后可以立即生效。
            List<String> permissionCodes = sysPermissionService.listPermissionCodesByUserId(userId, tenantId);

            // Spring Security 使用 GrantedAuthority 表示权限，因此将字符串权限码逐个转换。
            List<SimpleGrantedAuthority> authorities = permissionCodes.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            // LoginUser 是本项目给业务代码读取的登录用户快照。
            LoginUser loginUser = new LoginUser(
                    userId,
                    tenantId,
                    username,
                    departmentId,
                    permissionCodes
            );

            // 认证成功对象包含 principal（LoginUser）和 authorities（权限列表）。
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            loginUser,
                            null,
                            authorities
                    );

            // 写入当前请求线程的安全上下文，后续 SecurityUtils 和 @PreAuthorize 都从这里读取。
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException e) {
            // 防止当前线程残留无效认证信息，并返回统一 JSON 401。
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException("Invalid access token", e)
            );
        }
    }
}

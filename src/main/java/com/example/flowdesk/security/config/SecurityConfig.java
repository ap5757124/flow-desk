package com.example.flowdesk.security.config;


import com.example.flowdesk.security.filter.JwtAuthenticationFilter;
import com.example.flowdesk.security.handler.RestAccessDeniedHandler;
import com.example.flowdesk.security.handler.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 核心配置。
 *
 * <p>本项目使用无状态 JWT，不使用服务端 Session。登录和刷新接口公开，
 * 其余接口必须先通过 JWT 认证；方法级权限由 {@code @PreAuthorize} 判断。</p>
 */
@Configuration // 声明这是一个配置类，Spring 启动时会读取其中的 @Bean
@EnableMethodSecurity // 开启 @PreAuthorize 等方法级权限注解
@RequiredArgsConstructor // Lombok 为所有 final 字段生成构造器，供 Spring 完成依赖注入
public class SecurityConfig {

    /** 在每个请求进入 Controller 前解析 JWT 并建立登录上下文。 */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /** 处理未登录或 Token 无效的 401 响应。 */
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    /** 处理已登录但权限不足的 403 响应。 */
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    /**
     * 创建 BCrypt 密码编码器。
     * BCrypt 会自动加盐，因此同一个明文每次生成的哈希可以不同。
     */
    @Bean // 将返回对象注册为 Spring Bean，Service 可以注入 PasswordEncoder 接口
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /** 定义整个 HTTP 请求经过的安全过滤链。 */
    @Bean // 将 SecurityFilterChain 交给 Spring Security 使用
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // JWT 放在请求头中，不依赖 Cookie，因此这里关闭 CSRF 防护。
                .csrf(AbstractHttpConfigurer::disable)
                // 项目提供 JSON 登录接口，不使用 Spring 默认表单登录页。
                .formLogin(AbstractHttpConfigurer::disable)
                // 不启用浏览器弹窗式的 HTTP Basic 登录。
                .httpBasic(AbstractHttpConfigurer::disable)
                // 每次请求都依靠 JWT 认证，不在服务端保存 Session。
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // 登录和刷新 Token 时用户还没有 Access Token，因此允许匿名访问。
                        .requestMatchers("/auth/login",  "/auth/refresh").permitAll()
                        // 除上述白名单外，其他接口都必须完成认证。
                        .anyRequest().authenticated()
                )
                // 分别配置认证失败（401）和授权失败（403）的统一 JSON 响应。
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                )
                // JWT 必须先于用户名密码过滤器执行，才能提前建立 SecurityContext。
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


}

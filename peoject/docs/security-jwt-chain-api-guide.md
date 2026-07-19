# Security 和 JWT 链式 API 阅读指南

本文档解释项目中 `securityFilterChain`、`generateAccessToken`、`parseAccessToken` 这类链式调用怎么读，以及相关说明文档应该去哪里看。

## 链式调用是什么

链式调用常见于两类 API：

- Builder：一步一步填充参数，最后调用 `build()` 或 `compact()` 生成结果。
- DSL：用接近配置语言的方式描述规则，Spring Security 的 `HttpSecurity` 就是这种风格。

例如：

```java
return Jwts.builder()
        .subject("1")
        .claim("tenantId", 1)
        .issuedAt(now)
        .expiration(expiration)
        .signWith(secretKey)
        .compact();
```

可以按顺序读成：

```text
创建 JWT builder
 -> 设置 subject
 -> 设置自定义 claim
 -> 设置签发时间
 -> 设置过期时间
 -> 设置签名密钥
 -> 生成最终 token 字符串
```

关键点是：每一步通常都会返回当前 Builder 或配置对象，所以后面可以继续点下去。

## SecurityFilterChain 怎么读

项目中的 Security 配置：

```java
return http
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/auth/refresh").permitAll()
                .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
```

逐段解释：

| 调用 | 含义 |
| --- | --- |
| `csrf(AbstractHttpConfigurer::disable)` | 关闭 CSRF 防护。当前是前后端分离 JWT API，不使用 Session 表单登录。 |
| `formLogin(AbstractHttpConfigurer::disable)` | 关闭 Spring Security 默认表单登录页。 |
| `httpBasic(AbstractHttpConfigurer::disable)` | 关闭 HTTP Basic 认证。 |
| `sessionManagement(...)` | 配置 Session 管理策略。 |
| `SessionCreationPolicy.STATELESS` | 设置为无状态，不用服务端 Session 保存登录状态。 |
| `authorizeHttpRequests(...)` | 配置不同请求路径的访问规则。 |
| `requestMatchers("/auth/login", "/auth/refresh").permitAll()` | 登录和刷新 token 接口允许匿名访问。 |
| `anyRequest().authenticated()` | 其他所有请求都必须认证。 |
| `addFilterBefore(...)` | 把 JWT Filter 放到指定 Filter 之前执行。 |
| `build()` | 根据前面的配置生成最终的 `SecurityFilterChain` Bean。 |

为什么 `requestMatchers` 不写 `/ap/auth/login`：

```text
server.servlet.context-path=/ap 是 Servlet 容器层面的上下文路径。
Spring Security 匹配的是应用内部路径，所以写 /auth/login。
客户端真实访问时才是 /ap/auth/login。
```

## authorizeHttpRequests 怎么读

这一段是授权规则：

```java
.authorizeHttpRequests(auth -> auth
        .requestMatchers("/auth/login", "/auth/refresh").permitAll()
        .anyRequest().authenticated()
)
```

可以读成：

```text
开始配置 HTTP 请求授权
 -> 匹配 /auth/login 和 /auth/refresh
 -> 这些路径直接放行
 -> 其他任意请求
 -> 都要求已认证
```

规则顺序很重要。更具体的路径通常写在前面，兜底规则 `anyRequest()` 写在最后。

## addFilterBefore 怎么读

```java
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
```

含义是：把自定义的 `JwtAuthenticationFilter` 加到 Spring Security 过滤器链中，并且让它在 `UsernamePasswordAuthenticationFilter` 之前执行。

当前项目不使用默认用户名密码表单登录，但这个位置仍然常用来放 JWT Filter。这样请求进入 Controller 之前，JWT 已经被解析，`SecurityContextHolder` 中已经有当前登录用户。

请求链路可以理解为：

```text
HTTP Request
 -> Spring Security Filter Chain
 -> JwtAuthenticationFilter
 -> SecurityContextHolder 设置 Authentication
 -> Controller
```

## generateAccessToken 怎么读

项目中的 access token 生成逻辑：

```java
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
```

逐段解释：

| 调用 | 含义 |
| --- | --- |
| `Jwts.builder()` | 创建 JWT 构建器。 |
| `subject(...)` | 设置 JWT 的主体，当前使用用户 ID。 |
| `claim("tenantId", ...)` | 写入租户 ID，后续用于租户隔离。 |
| `claim("username", ...)` | 写入用户名，后续放入 `LoginUser`。 |
| `claim("departmentId", ...)` | 写入部门 ID，后续可用于组织或数据权限。 |
| `claim("tokenType", "access")` | 标记这是 access token。 |
| `issuedAt(now)` | 设置签发时间。 |
| `expiration(expiration)` | 设置过期时间。 |
| `signWith(getSecretKey())` | 使用密钥签名，防止 token 被篡改。 |
| `compact()` | 生成最终 JWT 字符串。 |

`subject` 和 `claim` 的区别：

- `subject` 是 JWT 标准字段，通常放用户 ID。
- `claim` 是自定义字段，可以放租户、用户名、部门、token 类型等业务信息。

## generateRefreshToken 怎么读

refresh token 和 access token 的生成方式类似，但用途不同：

```java
return Jwts.builder()
        .subject(String.valueOf(sysUser.getId()))
        .claim("tenantId", sysUser.getTenantId())
        .claim("username", sysUser.getUsername())
        .claim("tokenType", "refresh")
        .issuedAt(now)
        .expiration(expiration)
        .signWith(getSecretKey())
        .compact();
```

主要区别：

- `tokenType` 是 `refresh`。
- 过期时间更长。
- 不用于访问业务接口，只用于 `/auth/refresh` 换取新 token。

项目中通过 `tokenType` 避免 refresh token 被误当成 access token 使用。

## parseAccessToken 怎么读

项目中的 access token 解析逻辑：

```java
public Claims parseAccessToken(String token) {
    Claims claims = parseToken(token);

    if (!"access".equals(claims.get("tokenType", String.class))) {
        throw new JwtException("Invalid access token");
    }

    return claims;
}
```

可以读成：

```text
先解析并校验 token 签名和过期时间
 -> 再读取 tokenType
 -> 如果不是 access，则拒绝
 -> 返回 claims 给 Filter 使用
```

## JWT 解析链式调用怎么读

项目中的通用解析逻辑：

```java
return Jwts.parser()
        .verifyWith(getSecretKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
```

逐段解释：

| 调用 | 含义 |
| --- | --- |
| `Jwts.parser()` | 创建 JWT 解析器 Builder。 |
| `verifyWith(getSecretKey())` | 设置验签密钥。 |
| `build()` | 构建 JWT 解析器。 |
| `parseSignedClaims(token)` | 解析带签名的 JWT，并校验签名和过期时间。 |
| `getPayload()` | 取得 JWT 中的 claims 数据。 |

解析成功后拿到的 `Claims` 类似一个 Map，可以这样读取：

```java
String userId = claims.getSubject();
Integer tenantId = claims.get("tenantId", Integer.class);
String username = claims.get("username", String.class);
```

## Lambda 写法怎么看

Security 配置中有这种写法：

```java
.sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```

这是 Java Lambda。可以把它理解成：

```text
Spring Security 把 session 配置对象交给你，
你在 Lambda 里面继续配置它。
```

`authorizeHttpRequests(auth -> auth...)` 也是同样逻辑：Spring Security 把授权配置对象 `auth` 交给你，你在里面配置路径规则。

## 文档应该看哪里

优先看官方英文文档和当前版本 API：

- Spring Security Java Configuration: <https://docs.spring.io/spring-security/reference/servlet/configuration/java.html>
- Spring Security Authorize HTTP Requests: <https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html>
- JJWT README: <https://github.com/jwtk/jjwt/blob/main/README.adoc>
- JJWT Javadoc: <https://javadoc.io/doc/io.jsonwebtoken/jjwt-api>

中文资料可以辅助理解，例如搜索：

```text
Spring Security SecurityFilterChain 中文
Spring Security authorizeHttpRequests 中文
JJWT Jwts.builder 中文
```

但要注意：Spring Security 6 之后配置方式变化较大，很多中文博客仍然使用旧版 `WebSecurityConfigurerAdapter`。当前项目使用的是新的 `SecurityFilterChain` Bean 风格，所以最终应以官方文档、Javadoc 和项目当前依赖版本为准。

## 阅读链式 API 的方法

遇到不熟悉的链式调用，可以按这个顺序拆：

1. 看第一步创建了什么对象，例如 `http`、`Jwts.builder()`、`Jwts.parser()`。
2. 看中间每一步是在设置什么配置或字段。
3. 看最后一步是什么，通常是 `build()`、`compact()` 或返回一个结果。
4. 看方法名里的动词：`disable`、`permitAll`、`authenticated`、`signWith`、`parse`。
5. 不确定时点进源码或看 Javadoc，确认参数和返回值。

对本项目来说，可以把两条链路记成：

```text
SecurityFilterChain:
配置 HTTP 安全规则 -> 加 JWT Filter -> build
```

```text
JJWT:
写入 token 内容 -> 设置时间和签名 -> compact 生成 token
解析 token -> 验签 -> 读取 claims
```
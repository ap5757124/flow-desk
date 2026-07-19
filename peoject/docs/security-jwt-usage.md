# Security 和 JWT 使用说明

本文档整理 FlowDesk 当前已经完成的认证能力，重点说明 Spring Security、BCrypt、JWT、Refresh Token、用户上下文和租户隔离在项目中如何配合使用。

## 当前能力

当前 Security Foundation 已经覆盖：

- 登录接口 `/auth/login`
- BCrypt 密码校验
- 登录成功返回 `accessToken` 和 `refreshToken`
- JWT Filter 校验请求头中的 access token
- `LoginUser` 当前登录用户上下文
- `SecurityUtils` 统一获取当前用户信息
- 基于 `SecurityUtils.getTenantId()` 的租户数据过滤

项目配置了 `server.servlet.context-path: /ap`，所以实际访问路径示例是 `/ap/auth/login`、`/ap/sysUser/me`。但是 Spring Security 的 `requestMatchers` 里仍然写应用内部路径，例如 `/auth/login`，不用写 `/ap/auth/login`。

## 登录认证流程

登录入口是 `AuthController` 的 `/auth/login`。

整体流程：

```text
POST /ap/auth/login
 -> AuthController.login
 -> AuthServiceImpl.login
 -> SysUserService.findByTenantIdAndUsername
 -> PasswordEncoder.matches 校验 BCrypt 密码
 -> JwtTokenProvider.generateAccessToken
 -> JwtTokenProvider.generateRefreshToken
 -> 返回 LoginRes
```

请求示例：

```json
{
  "tenantId": 1,
  "username": "admin",
  "password": "123456"
}
```

返回示例：

```json
{
  "code": 200,
  "message": "请求成功",
  "data": {
    "userId": 1,
    "tenantId": 1,
    "username": "admin",
    "nickname": "管理员",
    "departmentId": 1,
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  },
  "timestamp": "2026-07-12T10:00:00"
}
```

登录校验的关键点：

- 用户必须存在。
- 用户状态不能是禁用状态。
- 数据库中保存的是 BCrypt 密文。
- 校验密码必须使用 `passwordEncoder.matches(rawPassword, encodedPassword)`。
- 登录成功后，access token 用于访问业务接口，refresh token 用于刷新登录状态。

## Access Token 请求认证流程

访问受保护接口时，前端需要在请求头中携带 access token：

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

后端认证流程：

```text
HTTP Request
 -> JwtAuthenticationFilter
 -> 读取 Authorization 请求头
 -> 截取 Bearer 后面的 token
 -> JwtTokenProvider.parseAccessToken
 -> 校验签名、过期时间、tokenType=access
 -> 从 claims 中读取 userId、tenantId、username、departmentId
 -> 构造 LoginUser
 -> 放入 SecurityContextHolder
 -> Controller / Service 通过 SecurityUtils 获取当前用户
```

当前 Filter 使用的是 `OncePerRequestFilter`，表示每个请求只执行一次 JWT 认证逻辑。

如果没有携带 token，请求会继续进入后续 Spring Security 流程，由 `.anyRequest().authenticated()` 判断是否需要认证。如果 token 无效、过期、签名错误或者不是 access token，Filter 会清空上下文并返回 401。

## Refresh Token 流程

refresh token 只用于刷新 token，不用于访问普通业务接口。

刷新入口是 `/auth/refresh`。

整体流程：

```text
POST /ap/auth/refresh
 -> AuthController.refresh
 -> AuthServiceImpl.refreshToken
 -> JwtTokenProvider.parseRefreshToken
 -> 校验签名、过期时间、tokenType=refresh
 -> 根据 userId 重新查询用户
 -> 校验用户存在且没有禁用
 -> 重新生成 accessToken 和 refreshToken
```

请求示例：

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

返回示例：

```json
{
  "code": 200,
  "message": "请求成功",
  "data": {
    "accessToken": "new-access-token",
    "refreshToken": "new-refresh-token"
  }
}
```

这里重新查询用户，是为了处理用户被删除、禁用等情况。即使 refresh token 还没过期，只要用户已经不可用，也不能继续签发新 token。

## SecurityFilterChain 配置含义

当前配置的核心目标是：前后端分离、无状态 JWT 认证。

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

含义：

- 关闭 CSRF：当前是前后端分离 API，不使用浏览器表单 Session 认证。
- 关闭 formLogin：不使用 Spring Security 默认登录页。
- 关闭 httpBasic：不使用浏览器弹窗式 Basic 认证。
- 设置无状态 Session：后端不依赖服务端 Session 保存登录状态。
- 放行 `/auth/login` 和 `/auth/refresh`。
- 其他接口都必须认证。
- 把 `JwtAuthenticationFilter` 放到用户名密码认证过滤器之前。

## 当前登录用户上下文

JWT Filter 解析成功后，会创建 `LoginUser` 并放入 Spring Security 上下文。

业务代码不直接操作 `SecurityContextHolder`，而是通过 `SecurityUtils` 获取：

```java
LoginUser loginUser = SecurityUtils.getLoginUser();
int userId = SecurityUtils.getUserId();
int tenantId = SecurityUtils.getTenantId();
String username = SecurityUtils.getUsername();
```

这样做的好处是：

- Controller 和 Service 不需要关心 Spring Security 底层对象。
- 后续如果 `LoginUser` 字段扩展，业务代码调用方式可以保持稳定。
- 认证失败时可以统一抛出 `BusinessException(ErrorCode.AUTHENTICATION_FAILED)`。

## 租户隔离使用方式

多租户查询不能相信前端传入的 `tenantId`，应该使用当前登录用户上下文中的租户。

当前用户列表查询已经按这个思路改造：

```java
int tenantId = SecurityUtils.getTenantId();

LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(SysUser::getTenantId, tenantId);

return sysUserMapper.selectList(queryWrapper);
```

这样 `/ap/sysUser/getList` 只会返回当前登录用户所属租户的数据。

后续工单、附件、评论、审计日志等业务表都应该遵守同样规则：

```text
前端不传 tenantId，或者即使传了也不作为数据范围依据。
后端从 SecurityUtils.getTenantId() 获取租户。
所有租户业务查询追加 tenant_id 条件。
```

## 常用接口

| 接口 | 是否需要 accessToken | 作用 |
| --- | --- | --- |
| `POST /ap/auth/login` | 否 | 登录，返回 accessToken 和 refreshToken |
| `POST /ap/auth/refresh` | 否 | 使用 refreshToken 换取新 token |
| `GET /ap/sysUser/me` | 是 | 查看当前登录用户上下文 |
| `GET /ap/sysUser/getList` | 是 | 查看当前租户下的用户列表 |

## 手工验证建议

1. 调用 `/ap/auth/login`，确认返回 `accessToken` 和 `refreshToken`。
2. 不带 token 访问 `/ap/sysUser/me`，应返回未认证。
3. 带 `Authorization: Bearer accessToken` 访问 `/ap/sysUser/me`，应返回当前用户。
4. 使用 `refreshToken` 调用 `/ap/auth/refresh`，应返回一组新 token。
5. 使用 `refreshToken` 直接访问 `/ap/sysUser/me`，应失败，因为它不是 access token。
6. 准备两个不同租户用户，分别登录后访问 `/ap/sysUser/getList`，应只看到当前租户数据。

## 当前边界

当前实现还没有包含：

- RBAC 角色权限
- 菜单权限
- 按钮权限
- Redis token 黑名单
- 退出登录 token 失效
- refresh token 轮换存储

这些属于后续权限体系和会话治理的扩展，不影响当前 JWT 认证主链路。
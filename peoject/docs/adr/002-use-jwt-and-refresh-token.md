# ADR-002: 使用 JWT 和 Refresh Token

## 状态

Proposed

## 背景

FlowDesk 需要支持前后端分离场景下的 API 认证，同时需要会话过期和 Token 刷新能力。

## 决策

使用短有效期 Access Token 和较长有效期 Refresh Token。

Access Token 用于普通 API 调用。Refresh Token 用于换取新的 Access Token。Refresh Token 轮换和退出登录失效可以通过 Redis 实现。

## 影响

收益：

- 适合前后端分离应用
- 减少每次请求查询数据库的成本
- 支持过期和刷新机制

代价：

- 退出登录和强制失效需要服务端 Token 状态或黑名单
- Token 泄露场景需要谨慎处理

## 当前落地约束

- Access Token 缺失、过期或签名无效时返回 HTTP 401，并使用统一的 `R` 响应体。
- Refresh Token 只能用于刷新，解析失败、类型错误或租户不匹配时返回 HTTP 401。
- 没有功能权限时返回 HTTP 403；认证和授权错误不再使用 HTTP 200 包装。

## 备选方案

- 服务端 Session：失效控制简单，但不如无状态 API 设计灵活。
- 长有效期单 JWT：实现简单，但安全性较弱。

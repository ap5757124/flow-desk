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

## 备选方案

- 服务端 Session：失效控制简单，但不如无状态 API 设计灵活。
- 长有效期单 JWT：实现简单，但安全性较弱。

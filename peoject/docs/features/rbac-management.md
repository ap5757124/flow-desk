# RBAC 角色权限管理

## 功能目标

本功能补齐系统管理中的 RBAC 管理接口，让租户管理员可以维护角色、权限，以及用户-角色、角色-权限两类授权关系。

RBAC 的核心链路是：

```text
用户 sys_user
 -> 用户角色关系 sys_user_role
 -> 角色 sys_role
 -> 角色权限关系 sys_role_permission
 -> 权限 sys_permission
 -> Spring Security @PreAuthorize 权限码判断
```

## API 契约

角色接口：

| 接口 | 权限码 | 说明 |
| --- | --- | --- |
| `GET /sysRole/list` | `system:role:list` | 查询当前租户未删除角色 |
| `POST /sysRole/create` | `system:role:create` | 新增当前租户角色 |
| `POST /sysRole/update` | `system:role:update` | 修改当前租户角色 |
| `POST /sysRole/status` | `system:role:update` | 启用或禁用当前租户角色 |
| `POST /sysRole/delete` | `system:role:delete` | 逻辑删除当前租户角色 |

权限接口：

| 接口 | 权限码 | 说明 |
| --- | --- | --- |
| `GET /sysPermission/list` | `system:permission:list` | 查询当前租户未删除权限 |
| `POST /sysPermission/create` | `system:permission:create` | 新增当前租户权限 |
| `POST /sysPermission/update` | `system:permission:update` | 修改当前租户权限 |
| `POST /sysPermission/status` | `system:permission:update` | 启用或禁用当前租户权限 |

关系接口：

| 接口 | 权限码 | 说明 |
| --- | --- | --- |
| `POST /sysUserRole/assign` | `system:user-role:update` | 给用户分配角色 |
| `POST /sysUserRole/remove` | `system:user-role:update` | 移除用户角色 |
| `POST /sysRolePermission/grant` | `system:role-permission:update` | 给角色授予权限 |
| `POST /sysRolePermission/remove` | `system:role-permission:update` | 移除角色权限 |

## 请求 DTO

所有写接口都使用请求 DTO，并在 Controller 方法参数上使用 `@Valid @RequestBody`。

当前请求 DTO 位于：

```text
src/main/java/com/example/flowdesk/system/dto/req
```

重要约定：

- DTO 不接收 `tenantId`。
- 当前租户必须从 `SecurityUtils.getTenantId()` 读取。
- ID 使用 `@NotNull` 和 `@Positive` 校验。
- `status` 使用 `@Min(0)` 和 `@Max(1)` 校验。
- 角色编码和权限编码使用正则限制格式。

## 权限和数据范围

功能权限由 Controller 上的 `@PreAuthorize` 处理。

数据范围由 Service 处理：

- 角色查询和写入都过滤 `tenant_id = 当前租户`、`deleted = 0`。
- 权限查询和写入都过滤 `tenant_id = 当前租户`、`deleted = 0`。
- 给用户分配角色前，校验用户和角色都属于当前租户。
- 给角色授予权限前，校验角色和权限都属于当前租户。
- 查不到当前租户内的数据时，统一返回“资源不存在”，避免暴露其他租户的数据是否存在。

## 核心业务规则

- 同一租户内，未删除角色的 `role_code` 不能重复。
- 同一租户内，未删除权限的 `permission_code` 不能重复。
- 同一租户内，用户和角色关系不能重复。
- 同一租户内，角色和权限关系不能重复。
- 角色逻辑删除时设置 `deleted = 1`，并同步设置 `status = 0`。
- 用户实时权限查询会忽略禁用或已删除角色、禁用或已删除权限。

## 事务边界

每个写 Service 方法都使用：

```java
@Transactional(rollbackFor = Exception.class)
```

事务覆盖：

- 业务校验。
- 数据库写入或删除。
- 写操作审计日志。

当前审计用结构化业务日志实现，没有单独审计表。后续如果新增 `sys_operation_log`，可以把这些 `log.info` 替换或扩展为审计表写入。

## 异常处理

本功能复用全局异常处理：

- 参数校验失败：`VALIDATION_ERROR`。
- 当前租户内查不到目标数据：`RESOURCE_NOT_FOUND`。
- 角色编码、权限编码或关系重复：`DUPLICATE_REQUEST`。
- 未登录或权限不足：由 Spring Security 返回认证或授权错误。

## 测试策略

建议补充 Service 层单元测试：

- 新增角色时自动使用当前租户。
- 修改其他租户角色返回资源不存在。
- 重复角色编码返回重复操作。
- 用户和角色不同租户时不能分配。
- 角色和权限不同租户时不能授权。
- 重复用户角色关系、重复角色权限关系会被拒绝。

建议补充 Controller 层测试：

- 缺少权限码时返回 403。
- DTO 字段缺失或格式错误时返回 400。

## 面试讲解点

可以这样讲：

> 我把 RBAC 管理拆成 Controller 和 Service 两层。Controller 负责 HTTP 路径、参数校验和 `@PreAuthorize` 功能权限；Service 负责当前租户校验、重复关系校验、事务边界和审计日志。租户 ID 不从请求里传，而是从认证上下文读取，避免客户端伪造租户。用户权限不是写死在 JWT 里，而是在 JWT 过滤器中实时查询，所以禁用角色或权限后可以尽快生效。

## 后续注意

- 数据库层建议给 `(tenant_id, role_code, deleted)`、`(tenant_id, permission_code, deleted)`、`(tenant_id, user_id, role_id)`、`(tenant_id, role_id, permission_id)` 增加唯一索引，防止并发请求绕过应用层重复校验。
- 后续可以新增审计表，把当前结构化日志升级为可查询的操作日志。
- 如果要区分平台管理员和租户管理员，需要在权限模型中增加平台级数据范围。

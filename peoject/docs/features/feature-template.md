# 功能名称

## 业务目标

描述这个功能要解决的业务问题，以及预期给用户带来的价值。

## 参与角色

列出谁可以使用这个功能。

示例：

- 普通用户
- 工单处理人
- 部门负责人
- 租户管理员
- 平台管理员

## API 设计

| Method | Path | Description | Permission |
| --- | --- | --- | --- |
| POST | `/api/example` | 示例接口 | `example:create` |

## 请求链路

```text
Request
 -> Authentication
 -> Permission check
 -> Parameter validation
 -> Business validation
 -> Transactional persistence
 -> Audit log or event
 -> Response
```

## 业务规则

- 规则 1
- 规则 2
- 规则 3

## 数据模型

涉及表：

- `example_table`

重要字段：

- `id`
- `tenant_id`
- `created_at`
- `updated_at`

## 事务边界

描述哪些操作必须一起成功或一起失败。

## 权限和数据范围

描述：

- 功能权限
- 租户隔离
- 部门或本人数据范围
- 特殊管理员场景

## 异常处理

| Scenario | Error Code | Message |
| --- | --- | --- |
| 参数无效 | `VALIDATION_ERROR` | 请求参数不合法 |

## 审计和日志

描述操作日志、时间线记录，以及重要日志字段。

## 异步事件

列出产生或消费的事件。

| Event | Producer | Consumer | Purpose |
| --- | --- | --- | --- |
| `ExampleCreatedEvent` | example 模块 | notification 模块 | 发送通知 |

## 测试用例

- 成功场景
- 无权限场景
- 参数无效场景
- 业务规则违反场景
- 并发或重复请求场景

## 面试讲解

总结这个功能在面试中应该如何讲。

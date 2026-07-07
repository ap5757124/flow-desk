# 工单创建功能

## 业务目标

用户可以提交内部支持工单。系统保存工单，绑定附件，记录时间线，并通知相关团队或处理人。

## 参与角色

- 普通用户
- 租户管理员
- 部门负责人

## API 设计

| Method | Path | Description | Permission |
| --- | --- | --- | --- |
| POST | `/api/tickets` | 创建工单 | `ticket:create` |
| GET | `/api/tickets/{id}` | 获取工单详情 | `ticket:view` |

## 请求链路

```text
POST /api/tickets
 -> JWT authentication
 -> Check ticket:create permission
 -> Validate title, category, priority, description, attachment IDs
 -> Resolve tenant and creator from user context
 -> Generate ticket number
 -> Save ticket
 -> Bind attachments
 -> Save timeline record
 -> Save outbox event
 -> Commit transaction
 -> Return ticket ID and ticket number
 -> Async publisher sends notification event
```

## 业务规则

- 禁用用户不能创建工单。
- 标题、分类和优先级必填。
- 附件 ID 必须属于同一租户，并且上传人或授权范围合法。
- 初始状态为 `NEW` 或 `PENDING_ASSIGNMENT`。
- 工单编号必须唯一。
- 工单创建必须写入时间线记录。

## 数据模型

涉及表：

- `ticket`
- `ticket_timeline`
- `file_attachment`
- `business_attachment`
- `outbox_event`

`ticket` 关键字段：

- `id`
- `tenant_id`
- `ticket_no`
- `title`
- `description`
- `category_id`
- `priority`
- `status`
- `creator_id`
- `department_id`
- `version`
- `created_at`

## 事务边界

以下操作必须在同一个事务中完成：

- 保存工单
- 绑定附件
- 保存时间线
- 保存 Outbox 事件

MQ 发布应该在事务提交后，由 Outbox publisher 异步完成。

## 权限和数据范围

- 用户必须拥有 `ticket:create` 权限。
- 租户 ID 来自已认证用户上下文。
- 用户不能绑定其他租户的文件。
- 除非是平台管理员，否则用户不能代表其他租户创建工单。

## 异常处理

| Scenario | Error Code | Message |
| --- | --- | --- |
| 标题缺失 | `VALIDATION_ERROR` | 工单标题不能为空 |
| 附件无效 | `FILE_ACCESS_DENIED` | 附件不存在或不可使用 |
| 重复请求 | `DUPLICATE_REQUEST` | 工单创建请求已处理 |
| 编号生成失败 | `TICKET_NO_GENERATE_FAILED` | 工单编号生成失败 |

## 审计和日志

时间线动作：

- `TICKET_CREATED`

重要日志字段：

- `ticket_id`
- `ticket_no`
- `tenant_id`
- `creator_id`
- `category_id`
- `priority`

## 异步事件

| Event | Producer | Consumer | Purpose |
| --- | --- | --- | --- |
| `TicketCreatedEvent` | ticket 模块 | notification 模块 | 通知相关用户 |

## 测试用例

- 成功创建工单。
- 创建工单时不传标题。
- 使用无效附件 ID 创建工单。
- 无权限用户创建工单。
- 重复创建请求返回已有结果或被安全拒绝。

## 面试讲解

讲这个功能时，重点讲完整后端链路：

> 创建工单接口先经过认证和权限校验，然后校验业务字段和附件归属。工单、附件绑定、时间线记录和 Outbox 事件在同一个事务中保存。通知不会在事务中直接发送，而是通过 Outbox 表异步发布，避免消息丢失。

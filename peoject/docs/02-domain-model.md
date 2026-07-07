# 领域模型

## 核心聚合

## User

表示系统账号。

关键字段：

- `id`
- `tenant_id`
- `username`
- `password_hash`
- `nickname`
- `email`
- `mobile`
- `status`
- `department_id`
- `created_at`
- `updated_at`

重要规则：

- 禁用用户不能登录。
- 用户权限来自角色。
- 用户数据访问范围取决于租户和部门。

## Role

表示一组权限。

关键字段：

- `id`
- `tenant_id`
- `code`
- `name`
- `status`

重要规则：

- 角色编码在租户内唯一。
- 如果同时存在平台角色和租户角色，需要明确区分。

## Permission

表示 API、菜单或操作权限。

关键字段：

- `id`
- `code`
- `name`
- `type`
- `resource`

重要规则：

- API 权限控制功能访问。
- 数据权限不能被 API 权限替代。

## Department

表示组织层级。

关键字段：

- `id`
- `tenant_id`
- `parent_id`
- `name`
- `path`
- `status`

重要规则：

- 部门层级用于支持数据范围校验。
- `path` 可以帮助高效查询下级部门。

## Ticket

表示核心业务对象工单。

关键字段：

- `id`
- `tenant_id`
- `ticket_no`
- `title`
- `description`
- `category_id`
- `priority`
- `status`
- `creator_id`
- `assignee_id`
- `department_id`
- `version`
- `created_at`
- `updated_at`
- `closed_at`

重要规则：

- 工单编号必须全局唯一或租户内唯一。
- 状态流转必须遵守工作流规则。
- 重要操作必须写入时间线记录。
- 并发更新应使用乐观锁。

## Ticket Timeline

表示工单生命周期中的所有重要业务动作。

关键字段：

- `id`
- `tenant_id`
- `ticket_id`
- `operator_id`
- `action_type`
- `from_status`
- `to_status`
- `content`
- `created_at`

动作示例：

- 工单创建
- 工单分派
- 状态变更
- 添加评论
- 上传附件
- 发送催办
- 工单关闭
- 提交评价

## Attachment

表示上传文件的元数据和业务绑定。

关键字段：

- `id`
- `tenant_id`
- `file_name`
- `content_type`
- `file_size`
- `storage_bucket`
- `storage_key`
- `uploader_id`
- `created_at`

绑定字段可放入独立关联表：

- `business_type`
- `business_id`
- `file_id`

## Notification

表示站内信或通知任务。

关键字段：

- `id`
- `tenant_id`
- `receiver_id`
- `title`
- `content`
- `channel`
- `read_status`
- `created_at`

## Outbox Event

表示等待可靠发布的领域事件。

关键字段：

- `id`
- `event_id`
- `tenant_id`
- `event_type`
- `aggregate_type`
- `aggregate_id`
- `payload`
- `status`
- `retry_count`
- `next_retry_at`
- `created_at`
- `updated_at`

重要规则：

- 业务数据和 Outbox 事件必须在同一个事务中保存。
- 事件消费者必须幂等。
- 失败事件必须可重试。

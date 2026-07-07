# 开发流程

## 标准功能流程

每个有意义的功能都使用这个流程：

```text
Requirement
 -> Feature design
 -> API contract
 -> Database design
 -> Business flow design
 -> Implementation
 -> Test or verification
 -> Documentation update
 -> Git commit
```

## 编码前

在 `docs/features/` 下创建或更新功能文档。

设计需要回答：

- 这个功能解决什么问题？
- 谁可以使用？
- 需要哪些 API endpoint？
- 会涉及哪些表？
- 事务边界是什么？
- 涉及哪些业务状态？
- 可能出现哪些错误？
- 需要哪些日志或审计记录？
- 是否需要异步事件？
- 如何测试？
- 面试时应该如何讲？

## 编码顺序

每个功能推荐按以下顺序实现：

1. 定义 DTO 和请求校验。
2. 定义或更新数据库 schema。
3. 实现 repository 或 mapper。
4. 实现领域规则。
5. 实现 application service。
6. 实现 controller。
7. 添加审计日志、事件或通知。
8. 添加测试。
9. 更新文档。

## Review Checklist

认为功能完成前，检查：

- API 命名清晰。
- 存在请求校验。
- 权限校验明确。
- 数据范围校验已应用。
- 业务状态校验已实现。
- 事务边界正确。
- 重要操作已记录日志。
- 已考虑重复请求或并发更新。
- 异常能返回有用的业务信息。
- 有测试或手工验证步骤。

## Git Commit 风格

推荐提交示例：

```text
feat(auth): add jwt login flow
feat(ticket): add ticket creation workflow
feat(workflow): add ticket state transition rules
feat(notification): add outbox event publisher
test(ticket): add ticket creation service tests
docs(adr): explain outbox consistency decision
```

## 推荐里程碑

## Milestone 1: Project Foundation

- Spring Boot 基础工程
- 统一响应包装
- 全局异常处理
- 参数校验
- API 文档
- 基础数据库迁移

## Milestone 2: Security Foundation

- 登录
- JWT
- Refresh token
- 密码加密
- Spring Security filter
- 用户上下文

## Milestone 3: System Management

- 用户
- 角色
- 权限
- 部门
- 字典

## Milestone 4: Ticket Core

- 创建工单
- 查询工单
- 分派工单
- 处理工单
- 确认工单
- 关闭工单
- 评价工单

## Milestone 5: Collaboration

- 评论
- 附件
- 时间线
- 催办

## Milestone 6: Reliability and Highlights

- 状态机
- Outbox 事件
- RabbitMQ 通知
- 幂等消费者
- 乐观锁
- 多租户过滤

## Milestone 7: Interview Polish

- README
- 架构图
- API 示例
- 数据库图
- 测试报告
- 面试亮点说明

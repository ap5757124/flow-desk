# FlowDesk

FlowDesk 是一个面向后端技术面试准备的 Spring Boot 后端项目。

它模拟一个真实的企业级工单协同平台：用户创建工单，团队分派和处理工单，协作者评论并上传附件，系统记录完整操作时间线，异步发送通知，并为管理者提供统计数据。

## 为什么选择这个项目

这个项目不是为了展示基础 CRUD，而是为了覆盖真实工作和技术面试中高频出现的后端能力：

- 认证与授权
- RBAC 和数据权限
- 多租户隔离
- 工单流程和状态机
- 事务设计
- 幂等设计
- 异步事件和最终一致性
- 文件存储
- 审计日志
- 缓存和查询优化
- 可观测性和测试

## 建议模块

- `common`：统一响应、异常、枚举、工具类
- `security`：Spring Security、JWT、登录、Token 刷新、权限校验
- `system`：用户、角色、部门、菜单、字典
- `tenant`：租户模型和租户隔离
- `ticket`：工单创建、分派、处理、关闭、评价
- `workflow`：工单状态机和流转规则
- `file`：文件元数据、MinIO 集成、附件绑定
- `notification`：站内信、邮件扩展点、MQ 消费者
- `audit`：操作日志和业务时间线
- `report`：统计报表和看板
- `infra`：Redis、RabbitMQ、对象存储、搜索、外部适配器

## 文档地图

- `AGENTS.md`：AI 协作规则和项目约束
- `docs/00-project-vision.md`：产品定位和面试目标
- `docs/01-architecture.md`：架构和模块边界
- `docs/02-domain-model.md`：核心领域模型
- `docs/03-development-workflow.md`：推荐开发流程
- `docs/04-interview-highlights.md`：面试讲解点
- `docs/features/`：功能设计文档
- `docs/adr/`：架构决策记录

## 推荐开发顺序

1. Spring Boot 基础工程搭建
2. 统一响应、异常处理、参数校验和 API 文档
3. 认证和 JWT
4. RBAC 和组织管理
5. 工单核心流程
6. 评论、附件和时间线
7. 通知和异步事件
8. Outbox 事件发布
9. 多租户隔离和数据权限
10. 统计、缓存和查询优化
11. 测试、Docker Compose 和面试文档整理

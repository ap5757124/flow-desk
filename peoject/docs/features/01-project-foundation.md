# Project Foundation 基础能力复盘

## 功能目标

本阶段属于 Milestone 1: Project Foundation，目标不是实现具体业务，而是先搭建 FlowDesk 后续所有接口都会复用的后端基础能力。

这一阶段重点解决三个问题：

- API 返回格式是否统一。
- 业务错误是否有明确错误码。
- 异常是否能集中处理，而不是散落在 Controller 中。

这些能力是后续认证、工单、租户、审计等模块的共同基础。

## 当前涉及文件

```text
src/main/java/com/example/flowdesk/common/response/R.java
src/main/java/com/example/flowdesk/common/exception/ErrorCode.java
src/main/java/com/example/flowdesk/common/exception/BusinessException.java
src/main/java/com/example/flowdesk/common/exception/GlobalExceptionHandler.java
pom.xml
```

## 统一响应 R

`R<T>` 是统一 API 响应对象。

它的作用是让所有接口返回同一种结构，避免每个 Controller 自己拼 JSON。

当前核心字段：

| 字段 | 含义 |
| --- | --- |
| `code` | 业务状态码，来自 `ErrorCode` |
| `message` | 响应提示信息 |
| `data` | 成功时返回的业务数据 |
| `traceId` | 链路追踪 ID，当前预留 |
| `timestamp` | 响应生成时间 |

当前创建响应的方式：

- `R.success(data)`：返回成功响应，并携带业务数据。
- `R.failed(code, message)`：返回失败响应。

设计思路：

- Controller 不直接拼响应结构。
- 成功和失败都通过静态方法创建。
- 后续如果加入 `traceId`，只需要在统一响应或全局处理处扩展。

## 错误码 ErrorCode

`ErrorCode` 负责集中维护系统中的错误类型。

当前已定义：

| ErrorCode | code | 含义 | 建议 HTTP 语义 |
| --- | ---: | --- | --- |
| `SUCCESS` | `200` | 请求成功 | 200 OK |
| `VALIDATION_ERROR` | `400` | 请求参数不合法 | 400 Bad Request |
| `AUTHENTICATION_FAILED` | `401` | 用户未登录、认证失败或 Token 无效 | 401 Unauthorized |
| `ACCESS_DENIED` | `403` | 已认证，但没有访问权限 | 403 Forbidden |
| `RESOURCE_NOT_FOUND` | `404` | 请求的数据不存在 | 404 Not Found |
| `SYSTEM_ERROR` | `500` | 系统未知异常 | 500 Internal Server Error |

设计思路：

- 错误码集中定义，避免到处写散装数字和字符串。
- 业务代码只需要关心抛出哪类错误。
- 全局异常处理器负责把错误码转换成统一响应。

待补充错误码：

| ErrorCode | code | 含义 |
| --- | ---: | --- |
| `BUSINESS_CONFLICT` | `409` | 当前业务状态不允许操作，例如已关闭工单不能再次处理 |
| `DUPLICATE_REQUEST` | `409` | 重复提交或重复操作 |

## 业务异常 BusinessException

`BusinessException` 是系统中的业务异常。

它继承 `RuntimeException`，内部持有一个 `ErrorCode`。

使用方式示例：

```java
throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
```

或使用自定义提示：

```java
throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
```

设计思路：

- 业务代码遇到错误时抛异常，而不是直接返回 `R.failed()`。
- `BusinessException` 不依赖 Controller，也不依赖具体业务模块。
- 异常到达 Web 层后，由 `GlobalExceptionHandler` 统一转换成响应。

这样可以保持业务代码干净，避免每个接口写重复的错误响应逻辑。

## 全局异常处理 GlobalExceptionHandler

`GlobalExceptionHandler` 使用 `@RestControllerAdvice` 统一处理接口异常。

当前处理三类异常：

| 异常类型 | 处理结果 |
| --- | --- |
| `BusinessException` | 使用异常中的 `ErrorCode` 和 message 返回失败响应 |
| `MethodArgumentNotValidException` | 返回 `VALIDATION_ERROR`，并取第一个字段校验错误提示 |
| `Exception` | 返回 `SYSTEM_ERROR`，避免未知异常堆栈暴露给前端 |

异常流转过程：

```text
Controller / Service 抛出异常
 -> Spring 捕获异常
 -> GlobalExceptionHandler 匹配异常类型
 -> 转换为 R.failed(code, message)
 -> 返回统一 JSON 响应
```

设计思路：

- Controller 不写 try-catch。
- 参数校验错误统一返回 `VALIDATION_ERROR`。
- 未知异常统一返回 `SYSTEM_ERROR`，避免内部实现细节泄露。

## 参数校验基础

项目已引入 Spring Validation 依赖，用于支持 `@Valid`、`@NotBlank` 等参数校验注解。

后续请求 DTO 示例：

```java
public class CreateTicketRequest {

    @NotBlank(message = "工单标题不能为空")
    private String title;
}
```

Controller 使用方式：

```java
public R<?> create(@Valid @RequestBody CreateTicketRequest request) {
    return R.success();
}
```

当参数校验失败时，Spring 会抛出 `MethodArgumentNotValidException`，再由 `GlobalExceptionHandler` 返回统一失败响应。

## 请求与异常整体流程

成功请求：

```text
HTTP Request
 -> Controller
 -> 执行业务逻辑
 -> R.success(data)
 -> HTTP Response
```

业务异常：

```text
HTTP Request
 -> Controller / Service
 -> throw new BusinessException(ErrorCode.xxx)
 -> GlobalExceptionHandler
 -> R.failed(code, message)
 -> HTTP Response
```

参数校验异常：

```text
HTTP Request
 -> @Valid 校验请求 DTO
 -> MethodArgumentNotValidException
 -> GlobalExceptionHandler
 -> R.failed(VALIDATION_ERROR)
 -> HTTP Response
```

未知异常：

```text
HTTP Request
 -> 代码执行时出现未知异常
 -> GlobalExceptionHandler
 -> R.failed(SYSTEM_ERROR)
 -> HTTP Response
```

## 权限和数据范围

本阶段不实现认证、授权和租户数据隔离。

但本阶段的错误码和异常结构已经为后续能力预留：

- `AUTHENTICATION_FAILED`：用于登录失败、Token 过期、Token 无效。
- `ACCESS_DENIED`：用于用户已登录但没有功能权限。
- `RESOURCE_NOT_FOUND`：用于数据不存在，或在数据权限范围内不可见。

后续进入 Security Foundation 时，可以直接复用这些错误码和统一响应。

## 数据库和事务

本阶段没有数据库表，也没有事务边界。

原因是 Project Foundation 只解决 Web 层通用响应、异常和校验问题。数据库、事务、租户过滤会在认证、系统管理或工单核心功能中逐步引入。

## 审计和日志

本阶段没有实现审计表和业务时间线。

后续需要补充：

- 未知异常记录错误日志。
- 重要业务异常记录业务上下文。
- 响应中的 `traceId` 接入 MDC 或请求过滤器。

## 异步事件

本阶段不涉及 MQ、Outbox 或异步事件。

Outbox 会在工单创建、通知投递等需要最终一致性的场景中实现。

## 当前待完善项

- `ErrorCode` 中文提示存在乱码，需要统一保存为 UTF-8。
- `ErrorCode` 建议补充 `BUSINESS_CONFLICT` 和 `DUPLICATE_REQUEST`。
- `FlowDeskApplication` 中仍有测试 Controller 职责，后续应拆到独立 Controller，启动类只负责启动应用。
- `GlobalExceptionHandler` 处理未知异常时后续应记录日志。
- `R.traceId` 当前只是预留字段，后续需要接入请求链路追踪。
- 健康检查接口建议放在 `common.web.HealthController`，不要放在启动类。

## 验证方式

可以通过以下方式验证本阶段能力：

1. 启动应用，确认 Spring Boot 正常启动。
2. 调用一个成功接口，确认返回结构中包含 `code`、`message`、`data`、`timestamp`。
3. 手动抛出 `BusinessException`，确认返回对应错误码和提示。
4. 使用 `@Valid` 请求 DTO 触发参数校验失败，确认返回 `VALIDATION_ERROR`。
5. 手动制造未知异常，确认返回 `SYSTEM_ERROR`，且不把堆栈直接暴露给前端。

## 面试讲解点

这部分可以这样讲：

> 我在写具体业务之前，先搭建了后端基础能力。所有接口统一返回 `R<T>`，错误类型集中放在 `ErrorCode`，业务异常通过 `BusinessException` 表达，再由 `GlobalExceptionHandler` 统一转换成响应。这样 Controller 不需要重复写 try-catch，也不会出现不同接口返回结构不一致的问题。后续认证、权限、工单和租户隔离都可以复用这套基础设施。

## 下一步建议

下一步建议继续完善 Milestone 1：

1. 清理启动类中的测试 Controller。
2. 新增 `common.web.HealthController`。
3. 为健康检查和异常处理补充基础测试。
4. 修复乱码并统一项目文件编码为 UTF-8。

我这一步先没有做具体业务，而是先搭 FlowDesk 的后端基础底座。因为这个项目定位是企业级工单系统，所以我先统一了响应模型 R、错误码 ErrorCode、业务异常 BusinessException 和全局异常处理 GlobalExceptionHandler，让后续所有接口都走同一套返回和错误处理规则。这样 Controller 只负责接请求，不再自己拼异常返回。
同时我也引入了参数校验，后面像工单创建这类接口可以直接靠 @Valid 和注解校验入参，不合法就统一返回 VALIDATION_ERROR。这个阶段本质上是在做项目的公共规范，先把响应、异常、校验这条链路定下来，后面再做认证、租户和工单流程时，就能直接沿用这套基础设施。


package com.example.flowdesk.security.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/** 登录接口请求参数。 */
@Data // Lombok 生成 getter 和 setter，供 JSON 反序列化及业务代码读取
public class LoginReq {
    /** 用户准备登录的租户 ID。 */
    @NotNull(message = "租户不能为空") // 校验字段不能为 null
    @Min(value = 1, message = "租户ID必须大于0") // 校验租户 ID 至少为 1
    private Integer tenantId;

    /** 登录账号，空字符串和纯空格都会校验失败。 */
    @NotBlank(message = "用户名不能为空") // 校验字符串必须包含至少一个非空白字符
    private String username;

    /** 用户输入的明文密码，只用于本次 BCrypt 比对，不会保存。 */
    @NotBlank(message = "密码不能为空") // 校验密码不能为空或纯空格
    private String password;

}

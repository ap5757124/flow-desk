package com.example.flowdesk.security.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class LoginReq {
    @NotNull(message = "租户不能为空")
    @Min(value = 1, message = "租户ID必须大于0")
    private Integer tenantId;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

}

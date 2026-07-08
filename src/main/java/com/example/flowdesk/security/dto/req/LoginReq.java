package com.example.flowdesk.security.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class LoginReq {
    @NotBlank(message = "租户不能为空")
    private int tenantId;
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "账号不能为空")
    private String password;

}

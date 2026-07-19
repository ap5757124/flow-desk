package com.example.flowdesk.security.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenReq {

    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}

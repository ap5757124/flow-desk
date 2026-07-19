package com.example.flowdesk.security.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** 刷新 Token 接口的请求参数。 */
@Data // Lombok 生成 getter 和 setter
public class RefreshTokenReq {

    /** 登录接口返回的 Refresh Token。 */
    @NotBlank(message = "刷新令牌不能为空") // 校验刷新令牌不能为空或纯空格
    private String refreshToken;
}

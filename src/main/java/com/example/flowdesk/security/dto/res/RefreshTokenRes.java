package com.example.flowdesk.security.dto.res;


import lombok.Data;

/** 刷新令牌成功后的响应数据。 */
@Data // Lombok 生成响应字段的 getter 和 setter
public class RefreshTokenRes {

    /** 新的 Access Token。 */
    private String accessToken;

    /** 新的 Refresh Token，用于令牌轮换。 */
    private String refreshToken;

}

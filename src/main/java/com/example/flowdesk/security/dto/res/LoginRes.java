package com.example.flowdesk.security.dto.res;

import lombok.Builder;
import lombok.Data;

@Data
public class LoginRes {
    private int userId;
    private int tenantId;
    private String username;
    private String nickname;
    private int departmentId;
    private String accessToken;
    private String refreshToken;
}

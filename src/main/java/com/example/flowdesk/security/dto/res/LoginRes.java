package com.example.flowdesk.security.dto.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRes {
    private int userId;
    private int tenantId;
    private String username;
    private String nickname;
    private int departmentId;
}

package com.example.flowdesk.security.context;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class LoginUser {

    /**
     * 用户id
     */
    private int userId;

    /**
     * 租户id
     */
    private int tenantId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 部门id
     */
    private int departmentId;

    /**
     * 权限code
     */
    private List<String> permissionCodes;


}

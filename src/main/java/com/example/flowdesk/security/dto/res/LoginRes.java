package com.example.flowdesk.security.dto.res;

import lombok.Data;

/** 登录成功后的响应数据，不包含密码及密码哈希。 */
@Data // Lombok 生成响应字段的 getter 和 setter
public class LoginRes {
    /** 登录用户主键 ID。 */
    private int userId;
    /** 登录用户所属租户 ID。 */
    private int tenantId;
    /** 登录用户名。 */
    private String username;
    /** 用户展示名称。 */
    private String nickname;
    /** 用户所属部门 ID。 */
    private int departmentId;
    /** 调用普通业务接口时放入 Authorization 请求头的短期令牌。 */
    private String accessToken;
    /** Access Token 过期后用于换取新令牌的长期令牌。 */
    private String refreshToken;
}

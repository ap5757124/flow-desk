package com.example.flowdesk.system.dto.res;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户接口对外响应 DTO。
 *
 * <p>这里故意不定义 password 字段，避免 BCrypt 密码哈希通过 API 泄露。</p>
 */
@Data // Lombok 生成响应字段的 getter 和 setter
public class SysUserRes {

    /** 用户主键 ID。 */
    private int id;
    /** 用户所属租户 ID。 */
    private int tenantId;
    /** 登录账号。 */
    private String username;
    /** 用户展示名称。 */
    private String nickname;
    /** 用户状态：1 启用，0 禁用。 */
    private String status;
    /** 用户所属部门 ID。 */
    private int departmentId;
    /** 用户创建时间。 */
    private LocalDateTime createTime;
    /** 用户最后更新时间。 */
    private LocalDateTime updateTime;
}

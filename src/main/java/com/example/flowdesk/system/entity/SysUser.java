package com.example.flowdesk.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * sys_user 表对应的持久化实体。
 *
 * <p>实体包含数据库字段，不能直接作为对外响应返回，尤其是 password 字段；
 * API 应使用 SysUserRes 这样的 DTO。</p>
 */
@Data // Lombok 生成 getter、setter 和常用 Object 方法
@TableName("sys_user") // 告诉 MyBatis-Plus 当前实体对应 sys_user 表
public class SysUser {
    /** 用户主键 ID。 */
    private int id ;
    /** 租户 ID，所有租户内查询都必须使用它过滤。 */
    private int tenantId ;
    /** 登录账号，通常在同一租户内唯一。 */
    private String username ;
    /** BCrypt 加密后的密码，绝不能返回给客户端。 */
    private String password ;
    /** 用户展示名称。 */
    private String nickname ;
    /** 用户状态：1 表示启用，0 表示禁用。 */
    private String status ;
    /** 所属部门 ID。 */
    private int departmentId ;
    /** 创建时间。 */
    private LocalDateTime createTime ;
    /** 最近更新时间。 */
    private LocalDateTime updateTime ;
}

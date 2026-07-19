package com.example.flowdesk.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** sys_role 表对应的角色实体。角色是权限的集合，并且属于某个租户。 */
@Data // Lombok 生成 getter、setter 和常用 Object 方法
@TableName("sys_role") // 映射到 sys_role 表
public class SysRole {
    /** 角色主键 ID。 */
    private int id ;
    /** 角色所属租户 ID。 */
    private int tenantId ;
    /** 角色展示名称。 */
    private String roleName ;
    /** 角色业务编码，例如 SUPPORT_AGENT。 */
    private String roleCode ;
    /** 角色用途说明。 */
    private String roleDesc ;
    /** 状态：1 启用，0 禁用；禁用角色不会产生权限。 */
    private byte status ;
    /** 创建时间。 */
    private LocalDateTime createTime ;
    /** 最近更新时间。 */
    private LocalDateTime updateTime ;
    /** 逻辑删除标记：1 已删除，0 未删除。 */
    private byte deleted ;
}

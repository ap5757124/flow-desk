package com.example.flowdesk.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** sys_role_permission 关联表实体：把角色和功能权限绑定起来。 */
@Data // Lombok 生成 getter、setter 和常用 Object 方法
@TableName("sys_role_permission") // 映射到 sys_role_permission 表
public class SysRolePermission {
    /** 关联记录主键 ID。 */
    @TableId(value = "id", type = IdType.AUTO) // MyBatis-Plus 注解：主键字段映射到 id 列，并使用数据库自增 ID
    private int id ;
    /** 关联所属租户 ID，必须与角色和权限的租户一致。 */
    private int tenantId ;
    /** 被授权的角色 ID。 */
    private int roleId ;
    /** 授予角色的权限 ID。 */
    private int permissionId ;
    /** 绑定创建时间。 */
    private LocalDateTime createTime ;
    /** 绑定最近更新时间。 */
    private LocalDateTime updateTime ;
}

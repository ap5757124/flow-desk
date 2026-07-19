package com.example.flowdesk.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role_permission")
public class SysRolePermission {
    /** 主键,; */
    private int id ;
    /** 租户ID,; */
    private int tenantId ;
    /** 角色ID,; */
    private int roleId ;
    /** 权限ID,; */
    private int permissionId ;
    /** 创建时间,; */
    private LocalDateTime createTime ;
    /** 更新时间,; */
    private LocalDateTime updateTime ;
}

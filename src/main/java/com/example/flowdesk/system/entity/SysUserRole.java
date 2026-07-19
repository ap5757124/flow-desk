package com.example.flowdesk.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user_role")
public class SysUserRole {
    /** 主键,; */
    private int id ;
    /** 租户ID,; */
    private int tenantId ;
    /** 用户ID,; */
    private int userId ;
    /** 角色ID,; */
    private int roleId ;
    /** 创建时间,; */
    private LocalDateTime createTime ;
    /** 更新时间,; */
    private LocalDateTime updateTime ;
}

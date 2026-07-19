package com.example.flowdesk.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role")
public class SysRole {
    /** 主键,; */
    private int id ;
    /** 租户ID,; */
    private int tenantId ;
    /** 角色名称,; */
    private String roleName ;
    /** 角色编码,; */
    private String roleCode ;
    /** 角色描述,; */
    private String roleDesc ;
    /** 状态：1启用，0禁用,; */
    private byte status ;
    /** 创建时间,; */
    private LocalDateTime createTime ;
    /** 更新时间,; */
    private LocalDateTime updateTime ;
    /** 是否删除,; */
    private byte deleted ;
}

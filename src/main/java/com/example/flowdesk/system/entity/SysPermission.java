package com.example.flowdesk.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_permission")
public class SysPermission {
    /** 主键,; */
    private int id ;
    /** 租户ID,; */
    private int tenantId ;
    /** 权限名称,; */
    private String permissionName ;
    /** 权限编码,; */
    private String permissionCode ;
    /** 权限描述,; */
    private String permissionDesc ;
    /** 状态：1启用，0禁用,; */
    private byte status ;
    /** 创建时间,; */
    private LocalDateTime createTime ;
    /** 更新时间,; */
    private LocalDateTime updateTime ;
    /** 是否删除,; */
    private byte deleted ;
}

package com.example.flowdesk.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    /** 用户主键 ID,; */
    private int id ;
    /** 租户 ID,; */
    private int tenantId ;
    /** 登录账号,; */
    private String username ;
    /** 加密后的密码,; */
    private String password ;
    /** 用户显示名,; */
    private String nickname ;
    /** 用户状态,; */
    private String status ;
    /** 所属部门 ID,; */
    private int departmentId ;
    /** 创建时间,; */
    private LocalDateTime createTime ;
    /** 更新时间,; */
    private LocalDateTime updateTime ;
}

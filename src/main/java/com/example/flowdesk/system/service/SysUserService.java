package com.example.flowdesk.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.flowdesk.system.entity.SysUser;

import java.util.List;

/** 用户业务服务，负责用户查询及用户相关用例。 */
public interface SysUserService extends IService<SysUser> {

    /** 在指定租户内按用户名查询用户，用于登录。 */
    SysUser findByTenantIdAndUsername(int tenantId, String username);

    /** 查询当前登录用户所属租户的全部用户。 */
    List<SysUser> listByCurrentTenant();
}

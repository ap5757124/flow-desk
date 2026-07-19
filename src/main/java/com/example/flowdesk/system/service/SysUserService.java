package com.example.flowdesk.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.flowdesk.system.entity.SysUser;

import java.util.List;

public interface SysUserService extends IService<SysUser> {
    SysUser findByTenantIdAndUsername(int tenantId, String username);

    List<SysUser> listByCurrentTenant();
}

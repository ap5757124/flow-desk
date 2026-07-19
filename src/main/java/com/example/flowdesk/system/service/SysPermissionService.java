package com.example.flowdesk.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.flowdesk.system.entity.SysPermission;

import java.util.List;

public interface SysPermissionService extends IService<SysPermission> {
    List<String> listPermissionCodesByUserId(int userId, int tenantId);
}

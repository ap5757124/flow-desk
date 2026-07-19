package com.example.flowdesk.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.flowdesk.system.entity.SysPermission;

import java.util.List;

/** 权限业务服务，负责权限实体和用户权限码查询。 */
public interface SysPermissionService extends IService<SysPermission> {

    /** 查询指定用户在指定租户内当前有效的权限码。 */
    List<String> listPermissionCodesByUserId(int userId, int tenantId);
}

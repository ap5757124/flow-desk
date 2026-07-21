package com.example.flowdesk.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.flowdesk.system.dto.req.SysPermissionCreateReq;
import com.example.flowdesk.system.dto.req.SysPermissionStatusReq;
import com.example.flowdesk.system.dto.req.SysPermissionUpdateReq;
import com.example.flowdesk.system.entity.SysPermission;

import java.util.List;

/** 权限业务服务，负责权限实体和用户权限码查询。 */
public interface SysPermissionService extends IService<SysPermission> {

    /** 查询指定用户在指定租户内当前有效的权限码。 */
    List<String> listPermissionCodesByUserId(int userId, int tenantId);

    /** 查询当前租户内未逻辑删除的权限列表。 */
    List<SysPermission> listByCurrentTenant();

    /** 在当前租户内新增权限。 */
    SysPermission createPermission(SysPermissionCreateReq req);

    /** 修改当前租户内未逻辑删除的权限。 */
    SysPermission updatePermission(SysPermissionUpdateReq req);

    /** 启用或禁用当前租户内未逻辑删除的权限。 */
    SysPermission updatePermissionStatus(SysPermissionStatusReq req);
}

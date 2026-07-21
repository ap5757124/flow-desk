package com.example.flowdesk.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.flowdesk.system.dto.req.SysRolePermissionReq;
import com.example.flowdesk.system.entity.SysRolePermission;

/** 角色权限关联业务服务。 */
public interface SysRolePermissionService extends IService<SysRolePermission> {

    /** 给当前租户内的角色授予当前租户内的权限。 */
    SysRolePermission grantPermissionToRole(SysRolePermissionReq req);

    /** 移除当前租户内角色与权限的绑定关系。 */
    void removePermissionFromRole(SysRolePermissionReq req);
}

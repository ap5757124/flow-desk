package com.example.flowdesk.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.flowdesk.system.dto.req.SysRoleCreateReq;
import com.example.flowdesk.system.dto.req.SysRoleDeleteReq;
import com.example.flowdesk.system.dto.req.SysRoleStatusReq;
import com.example.flowdesk.system.dto.req.SysRoleUpdateReq;
import com.example.flowdesk.system.entity.SysRole;

import java.util.List;

/** 角色业务服务；基础 CRUD 由 IService 提供。 */
public interface SysRoleService extends IService<SysRole> {

    /** 查询当前租户内未逻辑删除的角色列表。 */
    List<SysRole> listByCurrentTenant();

    /** 在当前租户内新增角色。 */
    SysRole createRole(SysRoleCreateReq req);

    /** 修改当前租户内未逻辑删除的角色。 */
    SysRole updateRole(SysRoleUpdateReq req);

    /** 启用或禁用当前租户内未逻辑删除的角色。 */
    SysRole updateRoleStatus(SysRoleStatusReq req);

    /** 逻辑删除当前租户内的角色。 */
    void deleteRole(SysRoleDeleteReq req);
}

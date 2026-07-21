package com.example.flowdesk.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.flowdesk.system.dto.req.SysUserRoleReq;
import com.example.flowdesk.system.entity.SysUserRole;

/** 用户角色关联业务服务。 */
public interface SysUserRoleService extends IService<SysUserRole> {

    /** 给当前租户内的用户分配当前租户内的角色。 */
    SysUserRole assignRoleToUser(SysUserRoleReq req);

    /** 移除当前租户内用户与角色的绑定关系。 */
    void removeRoleFromUser(SysUserRoleReq req);
}

package com.example.flowdesk.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.flowdesk.system.entity.SysRolePermission;
import com.example.flowdesk.system.mapper.SysRolePermissionMapper;
import com.example.flowdesk.system.service.SysRolePermissionService;
import org.springframework.stereotype.Service;

/** 角色权限关联业务服务的默认实现。 */
@Service // 注册为角色权限业务 Spring Bean
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission>
        implements SysRolePermissionService {
}

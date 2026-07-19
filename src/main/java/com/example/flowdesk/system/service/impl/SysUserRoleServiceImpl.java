package com.example.flowdesk.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.flowdesk.system.entity.SysUserRole;
import com.example.flowdesk.system.mapper.SysUserRoleMapper;
import com.example.flowdesk.system.service.SysUserRoleService;
import org.springframework.stereotype.Service;

/** 用户角色关联业务服务的默认实现。 */
@Service // 注册为用户角色业务 Spring Bean
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole>
        implements SysUserRoleService {
}

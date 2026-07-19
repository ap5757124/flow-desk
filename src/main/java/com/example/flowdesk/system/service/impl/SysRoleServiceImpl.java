package com.example.flowdesk.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.flowdesk.system.entity.SysRole;
import com.example.flowdesk.system.mapper.SysRoleMapper;
import com.example.flowdesk.system.service.SysRoleService;
import org.springframework.stereotype.Service;

/** 角色业务服务的默认实现，当前主要复用 IService 提供的基础 CRUD。 */
@Service // 注册为角色业务 Spring Bean
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
}

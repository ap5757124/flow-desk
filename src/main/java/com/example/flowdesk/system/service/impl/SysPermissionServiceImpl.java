package com.example.flowdesk.system.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.flowdesk.system.entity.SysPermission;
import com.example.flowdesk.system.mapper.SysPermissionMapper;
import com.example.flowdesk.system.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/** 权限业务服务实现，负责调用自定义 XML SQL 查询权限码。 */
@Service // 注册为权限业务 Spring Bean
@RequiredArgsConstructor // Lombok 生成 Mapper 构造器
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission>
        implements SysPermissionService {
    /** 权限 Mapper，内部 XML SQL 会连接用户角色、角色权限和权限表。 */
    private final SysPermissionMapper sysPermissionMapper;

    /** 将用户和租户条件传给 Mapper，返回当前有效权限码。 */
    @Override // 实现 SysPermissionService 的权限查询方法
    public List<String> listPermissionCodesByUserId(int userId, int tenantId) {
        return sysPermissionMapper.listPermissionCodesByUserId(userId, tenantId);
    }

}

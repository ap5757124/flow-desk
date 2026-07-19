package com.example.flowdesk.system.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.flowdesk.system.entity.SysPermission;
import com.example.flowdesk.system.mapper.SysPermissionMapper;
import com.example.flowdesk.system.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission>
        implements SysPermissionService {
    private final SysPermissionMapper sysPermissionMapper;

    @Override
    public List<String> listPermissionCodesByUserId(int userId, int tenantId) {
        return sysPermissionMapper.listPermissionCodesByUserId(userId, tenantId);
    }

}

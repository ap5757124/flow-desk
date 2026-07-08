package com.example.flowdesk.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.flowdesk.system.entity.SysUser;
import com.example.flowdesk.system.mapper.SysUserMapper;
import com.example.flowdesk.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper sysUserMapper;

    @Override
    public SysUser findByTenantIdAndUsername(int tenantId, String username) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(SysUser::getTenantId, tenantId)
                .eq(SysUser::getUsername, username);
        return sysUserMapper.selectOne(lambdaQueryWrapper);
    }
}

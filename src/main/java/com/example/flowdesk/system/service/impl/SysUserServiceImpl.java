package com.example.flowdesk.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.flowdesk.security.context.SecurityUtils;
import com.example.flowdesk.system.entity.SysUser;
import com.example.flowdesk.system.mapper.SysUserMapper;
import com.example.flowdesk.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/** 用户业务服务实现，继承 MyBatis-Plus 的通用 CRUD 能力。 */
@Service // 注册为用户业务 Spring Bean
@RequiredArgsConstructor // Lombok 生成 Mapper 构造器
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    /** 用户表 Mapper，执行按条件查询。 */
    private final SysUserMapper sysUserMapper;

    /** 登录时按“租户 + 用户名”定位账号，避免跨租户同名账号混淆。 */
    @Override // 实现 SysUserService 的登录查询方法
    public SysUser findByTenantIdAndUsername(int tenantId, String username) {
        // LambdaQueryWrapper 使用方法引用，重命名实体字段时比手写字符串更安全。
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(SysUser::getTenantId, tenantId)
                .eq(SysUser::getUsername, username);
        return sysUserMapper.selectOne(lambdaQueryWrapper);
    }

    /**
     * 查询当前租户的用户列表。
     * 租户 ID 从 SecurityContext 取得，不从请求参数取得，防止客户端越权切换租户。
     */
    @Override // 实现 SysUserService 的当前租户查询方法
    public List<SysUser> listByCurrentTenant() {
        int tenantId = SecurityUtils.getTenantId();

        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 这是租户隔离的关键条件；任何租户业务查询都必须有同类限制。
        lambdaQueryWrapper.eq(SysUser::getTenantId, tenantId);
        return sysUserMapper.selectList(lambdaQueryWrapper);
    }
}

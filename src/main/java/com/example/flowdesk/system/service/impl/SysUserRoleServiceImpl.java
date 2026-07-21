package com.example.flowdesk.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.flowdesk.common.exception.BusinessException;
import com.example.flowdesk.common.exception.ErrorCode;
import com.example.flowdesk.security.context.SecurityUtils;
import com.example.flowdesk.system.dto.req.SysUserRoleReq;
import com.example.flowdesk.system.entity.SysRole;
import com.example.flowdesk.system.entity.SysUser;
import com.example.flowdesk.system.entity.SysUserRole;
import com.example.flowdesk.system.mapper.SysRoleMapper;
import com.example.flowdesk.system.mapper.SysUserMapper;
import com.example.flowdesk.system.mapper.SysUserRoleMapper;
import com.example.flowdesk.system.service.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/** 用户角色关联业务服务实现，负责分配角色、移除角色、租户校验、重复校验和审计日志。 */
@Slf4j // Lombok 注解：自动生成 log 日志对象，用于记录关键写操作审计信息
@Service // Spring 注解：把当前类注册为 Service Bean，Controller 可以通过构造器注入它
@RequiredArgsConstructor // Lombok 注解：为 final 字段生成构造器，Spring 会用构造器注入 Mapper
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole>
        implements SysUserRoleService {

    /** 用户角色关联表 Mapper，负责关系表查询、插入和删除。 */
    private final SysUserRoleMapper sysUserRoleMapper;

    /** 用户表 Mapper，用于校验用户是否属于当前租户。 */
    private final SysUserMapper sysUserMapper;

    /** 角色表 Mapper，用于校验角色是否属于当前租户且未逻辑删除。 */
    private final SysRoleMapper sysRoleMapper;

    /** 给用户分配角色；用户和角色都必须属于当前租户。 */
    @Override // Java 注解：声明当前方法实现自 SysUserRoleService 接口
    @Transactional(rollbackFor = Exception.class) // Spring 事务注解：校验和插入关系任一步失败都会回滚
    public SysUserRole assignRoleToUser(SysUserRoleReq req) {
        int tenantId = SecurityUtils.getTenantId();
        requireCurrentTenantUser(req.getUserId(), tenantId);
        requireCurrentTenantRole(req.getRoleId(), tenantId);
        ensureUserRoleNotExists(req.getUserId(), req.getRoleId(), tenantId);

        LocalDateTime now = LocalDateTime.now();
        SysUserRole userRole = new SysUserRole();
        userRole.setTenantId(tenantId);
        userRole.setUserId(req.getUserId());
        userRole.setRoleId(req.getRoleId());
        userRole.setCreateTime(now);
        userRole.setUpdateTime(now);

        sysUserRoleMapper.insert(userRole);
        log.info("audit action=assign_role_to_user operatorUserId={} tenantId={} userId={} roleId={}",
                SecurityUtils.getUserId(), tenantId, userRole.getUserId(), userRole.getRoleId());
        return userRole;
    }

    /** 移除用户角色关系；只能移除当前租户范围内已经存在的关系。 */
    @Override // Java 注解：声明当前方法实现自 SysUserRoleService 接口
    @Transactional(rollbackFor = Exception.class) // Spring 事务注解：删除关系失败时回滚
    public void removeRoleFromUser(SysUserRoleReq req) {
        int tenantId = SecurityUtils.getTenantId();
        requireCurrentTenantUser(req.getUserId(), tenantId);
        requireCurrentTenantRole(req.getRoleId(), tenantId);

        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(SysUserRole::getTenantId, tenantId)
                .eq(SysUserRole::getUserId, req.getUserId())
                .eq(SysUserRole::getRoleId, req.getRoleId());

        Long count = sysUserRoleMapper.selectCount(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "用户角色关系不存在");
        }

        sysUserRoleMapper.delete(queryWrapper);
        log.info("audit action=remove_role_from_user operatorUserId={} tenantId={} userId={} roleId={}",
                SecurityUtils.getUserId(), tenantId, req.getUserId(), req.getRoleId());
    }

    /** 校验用户存在且属于当前租户。 */
    private void requireCurrentTenantUser(Integer userId, int tenantId) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(SysUser::getId, userId)
                .eq(SysUser::getTenantId, tenantId);

        if (sysUserMapper.selectCount(queryWrapper) == 0) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "用户不存在");
        }
    }

    /** 校验角色存在、属于当前租户，并且没有被逻辑删除。 */
    private void requireCurrentTenantRole(Integer roleId, int tenantId) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(SysRole::getId, roleId)
                .eq(SysRole::getTenantId, tenantId)
                .eq(SysRole::getDeleted, (byte) 0);

        if (sysRoleMapper.selectCount(queryWrapper) == 0) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "角色不存在");
        }
    }

    /** 校验同一租户内用户和角色的关系不能重复创建。 */
    private void ensureUserRoleNotExists(Integer userId, Integer roleId, int tenantId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(SysUserRole::getTenantId, tenantId)
                .eq(SysUserRole::getUserId, userId)
                .eq(SysUserRole::getRoleId, roleId);

        if (sysUserRoleMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE_REQUEST, "用户已拥有该角色");
        }
    }
}

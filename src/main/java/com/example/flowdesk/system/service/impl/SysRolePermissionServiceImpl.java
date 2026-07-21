package com.example.flowdesk.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.flowdesk.common.exception.BusinessException;
import com.example.flowdesk.common.exception.ErrorCode;
import com.example.flowdesk.security.context.SecurityUtils;
import com.example.flowdesk.system.dto.req.SysRolePermissionReq;
import com.example.flowdesk.system.entity.SysPermission;
import com.example.flowdesk.system.entity.SysRole;
import com.example.flowdesk.system.entity.SysRolePermission;
import com.example.flowdesk.system.mapper.SysPermissionMapper;
import com.example.flowdesk.system.mapper.SysRolePermissionMapper;
import com.example.flowdesk.system.mapper.SysRoleMapper;
import com.example.flowdesk.system.service.SysRolePermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/** 角色权限关联业务服务实现，负责角色授权、移除授权、租户校验、重复校验和审计日志。 */
@Slf4j // Lombok 注解：自动生成 log 日志对象，用于记录关键写操作审计信息
@Service // Spring 注解：把当前类注册为 Service Bean，Controller 可以通过构造器注入它
@RequiredArgsConstructor // Lombok 注解：为 final 字段生成构造器，Spring 会用构造器注入 Mapper
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission>
        implements SysRolePermissionService {

    /** 角色权限关联表 Mapper，负责关系表查询、插入和删除。 */
    private final SysRolePermissionMapper sysRolePermissionMapper;

    /** 角色表 Mapper，用于校验角色是否属于当前租户。 */
    private final SysRoleMapper sysRoleMapper;

    /** 权限表 Mapper，用于校验权限是否属于当前租户。 */
    private final SysPermissionMapper sysPermissionMapper;

    /** 给角色授予权限；角色和权限都必须属于当前租户。 */
    @Override // Java 注解：声明当前方法实现自 SysRolePermissionService 接口
    @Transactional(rollbackFor = Exception.class) // Spring 事务注解：校验和插入关系任一步失败都会回滚
    public SysRolePermission grantPermissionToRole(SysRolePermissionReq req) {
        int tenantId = SecurityUtils.getTenantId();
        requireCurrentTenantRole(req.getRoleId(), tenantId);
        requireCurrentTenantPermission(req.getPermissionId(), tenantId);
        ensureRolePermissionNotExists(req.getRoleId(), req.getPermissionId(), tenantId);

        LocalDateTime now = LocalDateTime.now();
        SysRolePermission rolePermission = new SysRolePermission();
        rolePermission.setTenantId(tenantId);
        rolePermission.setRoleId(req.getRoleId());
        rolePermission.setPermissionId(req.getPermissionId());
        rolePermission.setCreateTime(now);
        rolePermission.setUpdateTime(now);

        sysRolePermissionMapper.insert(rolePermission);
        log.info("audit action=grant_permission_to_role operatorUserId={} tenantId={} roleId={} permissionId={}",
                SecurityUtils.getUserId(), tenantId, rolePermission.getRoleId(), rolePermission.getPermissionId());
        return rolePermission;
    }

    /** 移除角色权限关系；只能移除当前租户范围内已经存在的关系。 */
    @Override // Java 注解：声明当前方法实现自 SysRolePermissionService 接口
    @Transactional(rollbackFor = Exception.class) // Spring 事务注解：删除关系失败时回滚
    public void removePermissionFromRole(SysRolePermissionReq req) {
        int tenantId = SecurityUtils.getTenantId();
        requireCurrentTenantRole(req.getRoleId(), tenantId);
        requireCurrentTenantPermission(req.getPermissionId(), tenantId);

        LambdaQueryWrapper<SysRolePermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(SysRolePermission::getTenantId, tenantId)
                .eq(SysRolePermission::getRoleId, req.getRoleId())
                .eq(SysRolePermission::getPermissionId, req.getPermissionId());

        Long count = sysRolePermissionMapper.selectCount(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "角色权限关系不存在");
        }

        sysRolePermissionMapper.delete(queryWrapper);
        log.info("audit action=remove_permission_from_role operatorUserId={} tenantId={} roleId={} permissionId={}",
                SecurityUtils.getUserId(), tenantId, req.getRoleId(), req.getPermissionId());
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

    /** 校验权限存在、属于当前租户，并且没有被逻辑删除。 */
    private void requireCurrentTenantPermission(Integer permissionId, int tenantId) {
        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(SysPermission::getId, permissionId)
                .eq(SysPermission::getTenantId, tenantId)
                .eq(SysPermission::getDeleted, (byte) 0);

        if (sysPermissionMapper.selectCount(queryWrapper) == 0) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "权限不存在");
        }
    }

    /** 校验同一租户内角色和权限的关系不能重复创建。 */
    private void ensureRolePermissionNotExists(Integer roleId, Integer permissionId, int tenantId) {
        LambdaQueryWrapper<SysRolePermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(SysRolePermission::getTenantId, tenantId)
                .eq(SysRolePermission::getRoleId, roleId)
                .eq(SysRolePermission::getPermissionId, permissionId);

        if (sysRolePermissionMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE_REQUEST, "角色已拥有该权限");
        }
    }
}

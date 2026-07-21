package com.example.flowdesk.system.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.flowdesk.common.exception.BusinessException;
import com.example.flowdesk.common.exception.ErrorCode;
import com.example.flowdesk.security.context.SecurityUtils;
import com.example.flowdesk.system.dto.req.SysPermissionCreateReq;
import com.example.flowdesk.system.dto.req.SysPermissionStatusReq;
import com.example.flowdesk.system.dto.req.SysPermissionUpdateReq;
import com.example.flowdesk.system.entity.SysPermission;
import com.example.flowdesk.system.mapper.SysPermissionMapper;
import com.example.flowdesk.system.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** 权限业务服务实现，负责权限码查询、权限管理、租户校验、事务和审计日志。 */
@Slf4j // Lombok 注解：自动生成 log 日志对象，用于记录关键写操作审计信息
@Service // Spring 注解：把当前类注册为 Service Bean，Controller 可以通过构造器注入它
@RequiredArgsConstructor // Lombok 注解：为 final 字段生成构造器，Spring 会用构造器注入 Mapper
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission>
        implements SysPermissionService {
    /** 权限 Mapper，内部 XML SQL 会连接用户角色、角色权限和权限表。 */
    private final SysPermissionMapper sysPermissionMapper;

    /** 将用户和租户条件传给 Mapper，返回当前有效权限码。 */
    @Override // Java 注解：声明当前方法实现自 SysPermissionService 接口
    public List<String> listPermissionCodesByUserId(int userId, int tenantId) {
        return sysPermissionMapper.listPermissionCodesByUserId(userId, tenantId);
    }

    /** 查询当前租户内未逻辑删除的权限列表。 */
    @Override // Java 注解：声明当前方法实现自 SysPermissionService 接口
    public List<SysPermission> listByCurrentTenant() {
        int tenantId = SecurityUtils.getTenantId();

        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(SysPermission::getTenantId, tenantId)
                .eq(SysPermission::getDeleted, (byte) 0)
                .orderByDesc(SysPermission::getCreateTime);
        return sysPermissionMapper.selectList(queryWrapper);
    }

    /** 新增权限，事务保证“重复校验 + 写入 + 审计日志”作为一个业务动作执行。 */
    @Override // Java 注解：声明当前方法实现自 SysPermissionService 接口
    @Transactional(rollbackFor = Exception.class) // Spring 事务注解：方法内出现异常时回滚数据库写入
    public SysPermission createPermission(SysPermissionCreateReq req) {
        int tenantId = SecurityUtils.getTenantId();
        ensurePermissionCodeUnique(tenantId, req.getPermissionCode(), null);

        LocalDateTime now = LocalDateTime.now();
        SysPermission permission = new SysPermission();
        permission.setTenantId(tenantId);
        permission.setPermissionName(req.getPermissionName());
        permission.setPermissionCode(req.getPermissionCode());
        permission.setPermissionDesc(req.getPermissionDesc());
        permission.setStatus((byte) 1);
        permission.setDeleted((byte) 0);
        permission.setCreateTime(now);
        permission.setUpdateTime(now);

        sysPermissionMapper.insert(permission);
        log.info("audit action=create_permission operatorUserId={} tenantId={} permissionId={} permissionCode={}",
                SecurityUtils.getUserId(), tenantId, permission.getId(), permission.getPermissionCode());
        return permission;
    }

    /** 修改权限基础信息，只允许操作当前租户内未删除的权限。 */
    @Override // Java 注解：声明当前方法实现自 SysPermissionService 接口
    @Transactional(rollbackFor = Exception.class) // Spring 事务注解：修改权限时异常会回滚
    public SysPermission updatePermission(SysPermissionUpdateReq req) {
        int tenantId = SecurityUtils.getTenantId();
        SysPermission permission = requireCurrentTenantPermission(req.getId(), tenantId);
        ensurePermissionCodeUnique(tenantId, req.getPermissionCode(), req.getId());

        permission.setPermissionName(req.getPermissionName());
        permission.setPermissionCode(req.getPermissionCode());
        permission.setPermissionDesc(req.getPermissionDesc());
        permission.setUpdateTime(LocalDateTime.now());

        sysPermissionMapper.updateById(permission);
        log.info("audit action=update_permission operatorUserId={} tenantId={} permissionId={} permissionCode={}",
                SecurityUtils.getUserId(), tenantId, permission.getId(), permission.getPermissionCode());
        return permission;
    }

    /** 启用或禁用权限；权限查询 SQL 会自动忽略禁用权限。 */
    @Override // Java 注解：声明当前方法实现自 SysPermissionService 接口
    @Transactional(rollbackFor = Exception.class) // Spring 事务注解：状态修改失败时回滚
    public SysPermission updatePermissionStatus(SysPermissionStatusReq req) {
        int tenantId = SecurityUtils.getTenantId();
        SysPermission permission = requireCurrentTenantPermission(req.getId(), tenantId);

        permission.setStatus(req.getStatus().byteValue());
        permission.setUpdateTime(LocalDateTime.now());

        sysPermissionMapper.updateById(permission);
        log.info("audit action=update_permission_status operatorUserId={} tenantId={} permissionId={} status={}",
                SecurityUtils.getUserId(), tenantId, permission.getId(), permission.getStatus());
        return permission;
    }

    /** 查询当前租户内未删除的权限；查不到时统一按不存在处理，避免暴露其他租户数据是否存在。 */
    private SysPermission requireCurrentTenantPermission(Integer permissionId, int tenantId) {
        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(SysPermission::getId, permissionId)
                .eq(SysPermission::getTenantId, tenantId)
                .eq(SysPermission::getDeleted, (byte) 0);

        SysPermission permission = sysPermissionMapper.selectOne(queryWrapper);
        if (permission == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "权限不存在");
        }
        return permission;
    }

    /** 校验同一租户内未删除权限的 permissionCode 不能重复。 */
    private void ensurePermissionCodeUnique(int tenantId, String permissionCode, Integer excludePermissionId) {
        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(SysPermission::getTenantId, tenantId)
                .eq(SysPermission::getPermissionCode, permissionCode)
                .eq(SysPermission::getDeleted, (byte) 0);
        if (excludePermissionId != null) {
            queryWrapper.ne(SysPermission::getId, excludePermissionId);
        }

        Long count = sysPermissionMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE_REQUEST, "权限编码已存在");
        }
    }

}

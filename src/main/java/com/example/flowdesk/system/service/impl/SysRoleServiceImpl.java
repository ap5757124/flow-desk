package com.example.flowdesk.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.flowdesk.common.exception.BusinessException;
import com.example.flowdesk.common.exception.ErrorCode;
import com.example.flowdesk.security.context.SecurityUtils;
import com.example.flowdesk.system.dto.req.SysRoleCreateReq;
import com.example.flowdesk.system.dto.req.SysRoleDeleteReq;
import com.example.flowdesk.system.dto.req.SysRoleStatusReq;
import com.example.flowdesk.system.dto.req.SysRoleUpdateReq;
import com.example.flowdesk.system.entity.SysRole;
import com.example.flowdesk.system.mapper.SysRoleMapper;
import com.example.flowdesk.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** 角色业务服务实现，负责角色管理的租户校验、重复校验、事务和审计日志。 */
@Slf4j // Lombok 注解：自动生成 log 日志对象，用于记录关键写操作审计信息
@Service // Spring 注解：把当前类注册为 Service Bean，Controller 可以通过构造器注入它
@RequiredArgsConstructor // Lombok 注解：为 final 字段生成构造器，Spring 会用构造器注入 Mapper
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    /** 角色表 Mapper，执行角色表的查询和写入。 */
    private final SysRoleMapper sysRoleMapper;

    /** 查询当前租户内未逻辑删除的角色列表。 */
    @Override // Java 注解：声明当前方法实现自 SysRoleService 接口，方法签名写错时编译器会提醒
    public List<SysRole> listByCurrentTenant() {
        int tenantId = SecurityUtils.getTenantId();

        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(SysRole::getTenantId, tenantId)
                .eq(SysRole::getDeleted, (byte) 0)
                .orderByDesc(SysRole::getCreateTime);
        return sysRoleMapper.selectList(queryWrapper);
    }

    /** 新增角色，事务保证“重复校验 + 写入 + 审计日志”作为一个业务动作执行。 */
    @Override // Java 注解：声明当前方法实现自 SysRoleService 接口
    @Transactional(rollbackFor = Exception.class) // Spring 事务注解：方法内出现异常时回滚数据库写入
    public SysRole createRole(SysRoleCreateReq req) {
        int tenantId = SecurityUtils.getTenantId();
        ensureRoleCodeUnique(tenantId, req.getRoleCode(), null);

        LocalDateTime now = LocalDateTime.now();
        SysRole role = new SysRole();
        role.setTenantId(tenantId);
        role.setRoleName(req.getRoleName());
        role.setRoleCode(req.getRoleCode());
        role.setRoleDesc(req.getRoleDesc());
        role.setStatus((byte) 1);
        role.setDeleted((byte) 0);
        role.setCreateTime(now);
        role.setUpdateTime(now);

        sysRoleMapper.insert(role);
        log.info("audit action=create_role operatorUserId={} tenantId={} roleId={} roleCode={}",
                SecurityUtils.getUserId(), tenantId, role.getId(), role.getRoleCode());
        return role;
    }

    /** 修改角色基础信息，只允许操作当前租户内未删除的角色。 */
    @Override // Java 注解：声明当前方法实现自 SysRoleService 接口
    @Transactional(rollbackFor = Exception.class) // Spring 事务注解：修改角色时异常会回滚
    public SysRole updateRole(SysRoleUpdateReq req) {
        int tenantId = SecurityUtils.getTenantId();
        SysRole role = requireCurrentTenantRole(req.getId(), tenantId);
        ensureRoleCodeUnique(tenantId, req.getRoleCode(), req.getId());

        role.setRoleName(req.getRoleName());
        role.setRoleCode(req.getRoleCode());
        role.setRoleDesc(req.getRoleDesc());
        role.setUpdateTime(LocalDateTime.now());

        sysRoleMapper.updateById(role);
        log.info("audit action=update_role operatorUserId={} tenantId={} roleId={} roleCode={}",
                SecurityUtils.getUserId(), tenantId, role.getId(), role.getRoleCode());
        return role;
    }

    /** 启用或禁用角色；权限查询 SQL 会自动忽略禁用角色。 */
    @Override // Java 注解：声明当前方法实现自 SysRoleService 接口
    @Transactional(rollbackFor = Exception.class) // Spring 事务注解：状态修改失败时回滚
    public SysRole updateRoleStatus(SysRoleStatusReq req) {
        int tenantId = SecurityUtils.getTenantId();
        SysRole role = requireCurrentTenantRole(req.getId(), tenantId);

        role.setStatus(req.getStatus().byteValue());
        role.setUpdateTime(LocalDateTime.now());

        sysRoleMapper.updateById(role);
        log.info("audit action=update_role_status operatorUserId={} tenantId={} roleId={} status={}",
                SecurityUtils.getUserId(), tenantId, role.getId(), role.getStatus());
        return role;
    }

    /** 逻辑删除角色；保留数据便于后续审计和排查。 */
    @Override // Java 注解：声明当前方法实现自 SysRoleService 接口
    @Transactional(rollbackFor = Exception.class) // Spring 事务注解：逻辑删除失败时回滚
    public void deleteRole(SysRoleDeleteReq req) {
        int tenantId = SecurityUtils.getTenantId();
        SysRole role = requireCurrentTenantRole(req.getId(), tenantId);

        role.setStatus((byte) 0);
        role.setDeleted((byte) 1);
        role.setUpdateTime(LocalDateTime.now());

        sysRoleMapper.updateById(role);
        log.info("audit action=delete_role operatorUserId={} tenantId={} roleId={} roleCode={}",
                SecurityUtils.getUserId(), tenantId, role.getId(), role.getRoleCode());
    }

    /** 查询当前租户内未删除的角色；查不到时统一按不存在处理，避免暴露其他租户数据是否存在。 */
    private SysRole requireCurrentTenantRole(Integer roleId, int tenantId) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(SysRole::getId, roleId)
                .eq(SysRole::getTenantId, tenantId)
                .eq(SysRole::getDeleted, (byte) 0);

        SysRole role = sysRoleMapper.selectOne(queryWrapper);
        if (role == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "角色不存在");
        }
        return role;
    }

    /** 校验同一租户内未删除角色的 roleCode 不能重复。 */
    private void ensureRoleCodeUnique(int tenantId, String roleCode, Integer excludeRoleId) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(SysRole::getTenantId, tenantId)
                .eq(SysRole::getRoleCode, roleCode)
                .eq(SysRole::getDeleted, (byte) 0);
        if (excludeRoleId != null) {
            queryWrapper.ne(SysRole::getId, excludeRoleId);
        }

        Long count = sysRoleMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE_REQUEST, "角色编码已存在");
        }
    }
}

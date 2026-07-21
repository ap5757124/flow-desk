package com.example.flowdesk.system.controller;

import com.example.flowdesk.common.response.R;
import com.example.flowdesk.system.dto.req.SysPermissionCreateReq;
import com.example.flowdesk.system.dto.req.SysPermissionStatusReq;
import com.example.flowdesk.system.dto.req.SysPermissionUpdateReq;
import com.example.flowdesk.system.entity.SysPermission;
import com.example.flowdesk.system.service.SysPermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 权限管理接口。
 *
 * <p>这里提供权限定义的管理能力。用户实际拥有的权限码仍由认证过滤器内部按
 * 当前用户和当前租户查询，不开放传 userId、tenantId 的公共查询接口。</p>
 */
@RestController // Spring Web 注解：声明这是 REST 控制器，方法返回对象会自动序列化为 JSON
@RequiredArgsConstructor // Lombok 注解：为 final 字段生成构造器，Spring 用它注入 SysPermissionService
@RequestMapping("/sysPermission") // Spring Web 注解：给本类所有接口统一添加 /sysPermission 路径前缀
public class SysPermissionController {

    /** 权限业务服务，负责真正的业务校验和数据库写入。 */
    private final SysPermissionService sysPermissionService;

    /** 查询当前租户下的权限列表。 */
    @PreAuthorize("hasAuthority('system:permission:list')") // Spring Security 注解：进入方法前校验当前用户是否拥有权限列表权限码
    @GetMapping("/list") // Spring Web 注解：接收 GET /sysPermission/list 请求
    public R<List<SysPermission>> list() {
        return R.success(sysPermissionService.listByCurrentTenant());
    }

    /** 新增当前租户下的权限。 */
    @PreAuthorize("hasAuthority('system:permission:create')") // Spring Security 注解：进入方法前校验当前用户是否拥有权限新增权限码
    @PostMapping("/create") // Spring Web 注解：接收 POST /sysPermission/create 请求
    public R<SysPermission> create(
            @Valid // Bean Validation 注解：触发 SysPermissionCreateReq 字段上的校验规则
            @RequestBody // Spring Web 注解：把请求体 JSON 反序列化成 SysPermissionCreateReq 对象
            SysPermissionCreateReq req
    ) {
        return R.success(sysPermissionService.createPermission(req));
    }

    /** 修改当前租户下的权限基础信息。 */
    @PreAuthorize("hasAuthority('system:permission:update')") // Spring Security 注解：进入方法前校验当前用户是否拥有权限修改权限码
    @PostMapping("/update") // Spring Web 注解：接收 POST /sysPermission/update 请求
    public R<SysPermission> update(
            @Valid // Bean Validation 注解：触发 SysPermissionUpdateReq 字段上的校验规则
            @RequestBody // Spring Web 注解：把请求体 JSON 反序列化成 SysPermissionUpdateReq 对象
            SysPermissionUpdateReq req
    ) {
        return R.success(sysPermissionService.updatePermission(req));
    }

    /** 启用或禁用当前租户下的权限。 */
    @PreAuthorize("hasAuthority('system:permission:update')") // Spring Security 注解：状态修改也属于权限修改权限
    @PostMapping("/status") // Spring Web 注解：接收 POST /sysPermission/status 请求
    public R<SysPermission> updateStatus(
            @Valid // Bean Validation 注解：触发 SysPermissionStatusReq 字段上的校验规则
            @RequestBody // Spring Web 注解：把请求体 JSON 反序列化成 SysPermissionStatusReq 对象
            SysPermissionStatusReq req
    ) {
        return R.success(sysPermissionService.updatePermissionStatus(req));
    }
}

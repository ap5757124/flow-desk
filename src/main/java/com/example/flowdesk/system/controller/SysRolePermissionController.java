package com.example.flowdesk.system.controller;

import com.example.flowdesk.common.response.R;
import com.example.flowdesk.system.dto.req.SysRolePermissionReq;
import com.example.flowdesk.system.entity.SysRolePermission;
import com.example.flowdesk.system.service.SysRolePermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 角色与权限关系管理接口。 */
@RestController // Spring Web 注解：声明这是 REST 控制器，方法返回对象会自动序列化为 JSON
@RequiredArgsConstructor // Lombok 注解：为 final 字段生成构造器，Spring 用它注入 SysRolePermissionService
@RequestMapping("/sysRolePermission") // Spring Web 注解：给本类所有接口统一添加 /sysRolePermission 路径前缀
public class SysRolePermissionController {

    /** 角色权限关联业务服务，负责同租户校验、重复关系校验和数据库写入。 */
    private final SysRolePermissionService sysRolePermissionService;

    /** 给当前租户内的角色授予当前租户内的权限。 */
    @PreAuthorize("hasAuthority('system:role-permission:update')") // Spring Security 注解：进入方法前校验当前用户是否拥有角色授权维护权限码
    @PostMapping("/grant") // Spring Web 注解：接收 POST /sysRolePermission/grant 请求
    public R<SysRolePermission> grant(
            @Valid // Bean Validation 注解：触发 SysRolePermissionReq 字段上的校验规则
            @RequestBody // Spring Web 注解：把请求体 JSON 反序列化成 SysRolePermissionReq 对象
            SysRolePermissionReq req
    ) {
        return R.success(sysRolePermissionService.grantPermissionToRole(req));
    }

    /** 移除当前租户内角色和权限的绑定关系。 */
    @PreAuthorize("hasAuthority('system:role-permission:update')") // Spring Security 注解：移除角色权限也属于角色授权维护权限
    @PostMapping("/remove") // Spring Web 注解：接收 POST /sysRolePermission/remove 请求
    public R<Void> remove(
            @Valid // Bean Validation 注解：触发 SysRolePermissionReq 字段上的校验规则
            @RequestBody // Spring Web 注解：把请求体 JSON 反序列化成 SysRolePermissionReq 对象
            SysRolePermissionReq req
    ) {
        sysRolePermissionService.removePermissionFromRole(req);
        return R.success(null);
    }
}

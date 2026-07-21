package com.example.flowdesk.system.controller;

import com.example.flowdesk.common.response.R;
import com.example.flowdesk.system.dto.req.SysUserRoleReq;
import com.example.flowdesk.system.entity.SysUserRole;
import com.example.flowdesk.system.service.SysUserRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 用户与角色关系管理接口。 */
@RestController // Spring Web 注解：声明这是 REST 控制器，方法返回对象会自动序列化为 JSON
@RequiredArgsConstructor // Lombok 注解：为 final 字段生成构造器，Spring 用它注入 SysUserRoleService
@RequestMapping("/sysUserRole") // Spring Web 注解：给本类所有接口统一添加 /sysUserRole 路径前缀
public class SysUserRoleController {

    /** 用户角色关联业务服务，负责同租户校验、重复关系校验和数据库写入。 */
    private final SysUserRoleService sysUserRoleService;

    /** 给当前租户内的用户分配当前租户内的角色。 */
    @PreAuthorize("hasAuthority('system:user-role:update')") // Spring Security 注解：进入方法前校验当前用户是否拥有用户角色维护权限码
    @PostMapping("/assign") // Spring Web 注解：接收 POST /sysUserRole/assign 请求
    public R<SysUserRole> assign(
            @Valid // Bean Validation 注解：触发 SysUserRoleReq 字段上的校验规则
            @RequestBody // Spring Web 注解：把请求体 JSON 反序列化成 SysUserRoleReq 对象
            SysUserRoleReq req
    ) {
        return R.success(sysUserRoleService.assignRoleToUser(req));
    }

    /** 移除当前租户内用户和角色的绑定关系。 */
    @PreAuthorize("hasAuthority('system:user-role:update')") // Spring Security 注解：移除用户角色也属于用户角色维护权限
    @PostMapping("/remove") // Spring Web 注解：接收 POST /sysUserRole/remove 请求
    public R<Void> remove(
            @Valid // Bean Validation 注解：触发 SysUserRoleReq 字段上的校验规则
            @RequestBody // Spring Web 注解：把请求体 JSON 反序列化成 SysUserRoleReq 对象
            SysUserRoleReq req
    ) {
        sysUserRoleService.removeRoleFromUser(req);
        return R.success(null);
    }
}

package com.example.flowdesk.system.controller;

import com.example.flowdesk.common.response.R;
import com.example.flowdesk.system.dto.req.SysRoleCreateReq;
import com.example.flowdesk.system.dto.req.SysRoleDeleteReq;
import com.example.flowdesk.system.dto.req.SysRoleStatusReq;
import com.example.flowdesk.system.dto.req.SysRoleUpdateReq;
import com.example.flowdesk.system.entity.SysRole;
import com.example.flowdesk.system.service.SysRoleService;
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
 * 角色管理接口。
 *
 * <p>Controller 只负责 HTTP 入参、权限注解和统一响应，租户校验、重复校验、
 * 事务和审计日志都放在 SysRoleService 中。</p>
 */
@RestController // Spring Web 注解：声明这是 REST 控制器，方法返回对象会自动序列化为 JSON
@RequiredArgsConstructor // Lombok 注解：为 final 字段生成构造器，Spring 用它注入 SysRoleService
@RequestMapping("/sysRole") // Spring Web 注解：给本类所有接口统一添加 /sysRole 路径前缀
public class SysRoleController {

    /** 角色业务服务，负责真正的业务校验和数据库写入。 */
    private final SysRoleService sysRoleService;

    /** 查询当前租户下的角色列表。 */
    @PreAuthorize("hasAuthority('system:role:list')") // Spring Security 注解：进入方法前校验当前用户是否拥有角色列表权限码
    @GetMapping("/list") // Spring Web 注解：接收 GET /sysRole/list 请求
    public R<List<SysRole>> list() {
        return R.success(sysRoleService.listByCurrentTenant());
    }

    /** 新增当前租户下的角色。 */
    @PreAuthorize("hasAuthority('system:role:create')") // Spring Security 注解：进入方法前校验当前用户是否拥有角色新增权限码
    @PostMapping("/create") // Spring Web 注解：接收 POST /sysRole/create 请求
    public R<SysRole> create(
            @Valid // Bean Validation 注解：触发 SysRoleCreateReq 字段上的校验规则
            @RequestBody // Spring Web 注解：把请求体 JSON 反序列化成 SysRoleCreateReq 对象
            SysRoleCreateReq req
    ) {
        return R.success(sysRoleService.createRole(req));
    }

    /** 修改当前租户下的角色基础信息。 */
    @PreAuthorize("hasAuthority('system:role:update')") // Spring Security 注解：进入方法前校验当前用户是否拥有角色修改权限码
    @PostMapping("/update") // Spring Web 注解：接收 POST /sysRole/update 请求
    public R<SysRole> update(
            @Valid // Bean Validation 注解：触发 SysRoleUpdateReq 字段上的校验规则
            @RequestBody // Spring Web 注解：把请求体 JSON 反序列化成 SysRoleUpdateReq 对象
            SysRoleUpdateReq req
    ) {
        return R.success(sysRoleService.updateRole(req));
    }

    /** 启用或禁用当前租户下的角色。 */
    @PreAuthorize("hasAuthority('system:role:update')") // Spring Security 注解：状态修改也属于角色修改权限
    @PostMapping("/status") // Spring Web 注解：接收 POST /sysRole/status 请求
    public R<SysRole> updateStatus(
            @Valid // Bean Validation 注解：触发 SysRoleStatusReq 字段上的校验规则
            @RequestBody // Spring Web 注解：把请求体 JSON 反序列化成 SysRoleStatusReq 对象
            SysRoleStatusReq req
    ) {
        return R.success(sysRoleService.updateRoleStatus(req));
    }

    /** 逻辑删除当前租户下的角色。 */
    @PreAuthorize("hasAuthority('system:role:delete')") // Spring Security 注解：进入方法前校验当前用户是否拥有角色删除权限码
    @PostMapping("/delete") // Spring Web 注解：接收 POST /sysRole/delete 请求
    public R<Void> delete(
            @Valid // Bean Validation 注解：触发 SysRoleDeleteReq 字段上的校验规则
            @RequestBody // Spring Web 注解：把请求体 JSON 反序列化成 SysRoleDeleteReq 对象
            SysRoleDeleteReq req
    ) {
        sysRoleService.deleteRole(req);
        return R.success(null);
    }
}

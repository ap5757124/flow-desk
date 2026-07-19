package com.example.flowdesk.system.controller;


import com.example.flowdesk.system.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限管理接口预留类。
 *
 * <p>权限码查询目前仅供认证过滤器内部使用，不应直接暴露可传 userId、tenantId 的公共接口，
 * 否则客户端可能尝试查询其他用户或租户的权限。</p>
 */
@RestController // 声明 REST 控制器
@RequiredArgsConstructor // Lombok 生成 SysPermissionService 构造器
@RequestMapping("/sysPermission") // 为后续权限管理接口预留统一路径前缀
public class SysPermissionController {
    /** 权限业务服务。 */
    private final SysPermissionService sysPermissionService;

}

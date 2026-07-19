package com.example.flowdesk.system.controller;


import com.example.flowdesk.system.service.SysRolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 角色与权限关系管理接口预留类。 */
@RestController // 声明 REST 控制器
@RequiredArgsConstructor // Lombok 生成 SysRolePermissionService 构造器
@RequestMapping("/SysRolePermission") // 为后续角色授权接口预留路径前缀
public class SysRolePermissionController {
    /** 角色权限关联业务服务。 */
    private final SysRolePermissionService sysRolePermissionService;
}

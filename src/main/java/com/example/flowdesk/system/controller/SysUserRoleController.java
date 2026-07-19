package com.example.flowdesk.system.controller;

import com.example.flowdesk.system.service.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 用户与角色关系管理接口预留类。 */
@RestController // 声明 REST 控制器
@RequiredArgsConstructor // Lombok 生成 SysUserRoleService 构造器
@RequestMapping("/sysUserRole") // 为后续用户角色分配接口预留路径前缀
public class SysUserRoleController {
    /** 用户角色关联业务服务。 */
    private final SysUserRoleService sysUserRoleService;
}

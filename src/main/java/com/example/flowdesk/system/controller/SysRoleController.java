package com.example.flowdesk.system.controller;

import com.example.flowdesk.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色管理接口预留类。
 *
 * <p>当前还没有对外方法；后续增加角色接口时，应同时补权限校验和租户范围校验。</p>
 */
@RestController // 声明 REST 控制器
@RequiredArgsConstructor // Lombok 生成 SysRoleService 构造器
@RequestMapping("/sysRole") // 为后续角色接口预留统一路径前缀
public class SysRoleController {
    /** 角色业务服务。 */
    private final SysRoleService sysRoleService;

}

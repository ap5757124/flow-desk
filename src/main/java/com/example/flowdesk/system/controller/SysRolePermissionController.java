package com.example.flowdesk.system.controller;


import com.example.flowdesk.system.service.SysRolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/SysRolePermission")
public class SysRolePermissionController {
    private final SysRolePermissionService sysRolePermissionService;
}

package com.example.flowdesk.system.controller;

import com.example.flowdesk.system.service.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sysUserRole")
public class SysUserRoleController {
    private final SysUserRoleService sysUserRoleService;
}

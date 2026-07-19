package com.example.flowdesk.system.controller;

import com.example.flowdesk.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sysRole")
public class SysRoleController {
    private final SysRoleService sysRoleService;

}

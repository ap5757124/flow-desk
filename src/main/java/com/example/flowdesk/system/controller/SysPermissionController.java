package com.example.flowdesk.system.controller;


import com.example.flowdesk.common.response.R;
import com.example.flowdesk.system.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sysPermission")
public class SysPermissionController {
    private final SysPermissionService sysPermissionService;


//    public R<List<String>> listPermissionCodesByUserId(int userId, int tenantId) {
//        List<String>  permissionCodes = sysPermissionService.listPermissionCodesByUserId(userId, tenantId);
//        return R.success(permissionCodes);
//    }

}

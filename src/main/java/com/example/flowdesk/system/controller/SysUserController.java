package com.example.flowdesk.system.controller;

import com.example.flowdesk.common.response.R;
import com.example.flowdesk.security.context.LoginUser;
import com.example.flowdesk.security.context.SecurityUtils;
import com.example.flowdesk.system.entity.SysUser;
import com.example.flowdesk.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/sysUser")
public class SysUserController {


    private final SysUserService sysUserService;

    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/list")
    public R<List<SysUser>> list() {
        List<SysUser> list = sysUserService.listByCurrentTenant();
        return R.success(list);
    }

    @GetMapping("/me")
    public R<LoginUser> me() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return R.success(loginUser);
    }

}


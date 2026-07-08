package com.example.flowdesk.system.controller;

import com.example.flowdesk.common.response.R;
import com.example.flowdesk.system.entity.SysUser;
import com.example.flowdesk.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/sysUser")
public class SysUserController {


    private final SysUserService sysUserService;


    @GetMapping("/getList")
    public R<List<SysUser>> getList() {
        List<SysUser> list = sysUserService.list();
        return R.success(list);
    }

}

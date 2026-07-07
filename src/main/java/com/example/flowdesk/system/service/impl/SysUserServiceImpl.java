package com.example.flowdesk.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.flowdesk.system.entity.SysUser;
import com.example.flowdesk.system.mapper.SysUserMapper;
import com.example.flowdesk.system.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
}

package com.example.flowdesk.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.flowdesk.security.dto.req.LoginReq;
import com.example.flowdesk.security.dto.res.LoginRes;

public interface AuthService {
    LoginRes login(LoginReq loginReq);
}

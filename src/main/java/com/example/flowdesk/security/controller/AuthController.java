package com.example.flowdesk.security.controller;


import com.example.flowdesk.common.response.R;
import com.example.flowdesk.security.dto.req.LoginReq;
import com.example.flowdesk.security.dto.res.LoginRes;
import com.example.flowdesk.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "login", name = "认证登录")
    @Valid
    public R<LoginRes> login(@RequestBody LoginReq loginReq) {
        LoginRes loginRes = authService.login(loginReq);
        return R.success(loginRes);
    }

}

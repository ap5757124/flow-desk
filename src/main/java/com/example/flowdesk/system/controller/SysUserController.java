package com.example.flowdesk.system.controller;

import com.example.flowdesk.common.response.R;
import com.example.flowdesk.security.context.LoginUser;
import com.example.flowdesk.security.context.SecurityUtils;
import com.example.flowdesk.system.dto.res.SysUserRes;
import com.example.flowdesk.system.mapstruct.SysUserResStructMapper;
import com.example.flowdesk.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 用户查询接口，演示功能权限和租户数据隔离的完整链路。 */
@RequiredArgsConstructor // Lombok 生成 final 字段构造器，Spring 自动注入 Service 和 Mapper
@RestController // 声明 REST 控制器，返回对象自动序列化为 JSON
@RequestMapping("/sysUser") // 为本类接口添加 /sysUser 路径前缀
public class SysUserController {

    /** 用户业务服务，负责按当前租户查询数据。 */
    private final SysUserService sysUserService;

    /** 将用户实体转换为不含密码的响应 DTO。 */
    private final SysUserResStructMapper sysUserResStructMapper;

    /** 查询当前登录用户所属租户的用户列表。 */
    @PreAuthorize("hasAuthority('system:user:list')") // 方法执行前检查用户是否拥有用户列表权限
    @GetMapping("/list") // 接收 GET /sysUser/list 请求
    public R<List<SysUserRes>> list() {
        // Service 负责租户过滤，MapStruct 负责删除密码等敏感响应字段。
        return R.success(sysUserResStructMapper.toResponseList(sysUserService.listByCurrentTenant()));
    }

    /** 返回 JWT 过滤器放入安全上下文的当前登录用户。 */
    @GetMapping("/me") // 接收 GET /sysUser/me 请求；只要求登录，不要求额外功能权限
    public R<LoginUser> me() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return R.success(loginUser);
    }

}


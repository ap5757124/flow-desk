package com.example.flowdesk.security.context;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 当前请求中的登录用户快照。
 *
 * <p>JWT 过滤器解析令牌后创建该对象，并把它放入 Spring Security 上下文。
 * 它只在当前请求生命周期内使用，不是数据库实体。</p>
 */
@Data // Lombok 生成字段访问方法和常用 Object 方法
@AllArgsConstructor // Lombok 生成包含全部字段的构造器，便于过滤器一次性创建对象
public class LoginUser {

    /**
     * 用户主键 ID，对应 sys_user.id。
     */
    private int userId;

    /**
     * 当前用户所属租户 ID，后续业务查询必须使用它做数据隔离。
     */
    private int tenantId;

    /**
     * 登录用户名。
     */
    private String username;

    /**
     * 用户所属部门 ID，后续可用于部门级数据权限。
     */
    private int departmentId;

    /**
     * 当前请求中用户拥有的权限码，例如 system:user:list。
     */
    private List<String> permissionCodes;


}

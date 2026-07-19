package com.example.flowdesk;

import com.example.flowdesk.system.entity.SysUser;
import com.example.flowdesk.system.service.SysUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/** Spring Boot 应用上下文和基础数据库能力测试。 */
@SpringBootTest // 启动完整 Spring 容器，测试真实 Bean 装配和数据库连接
class FlowDeskApplicationTests {

    /** 验证 Spring 容器可以正常启动。 */
    @Test // 标记为 JUnit 测试方法
    void contextLoads() {
    }


    @Autowired // 从测试用 Spring 容器注入真实用户服务
    private SysUserService sysUserService;

    @Autowired // 从测试用 Spring 容器注入 BCrypt 密码编码器
    private PasswordEncoder passwordEncoder;

    /** 开发期数据库连通性检查；输出实体时可能包含密码哈希，不应复制到生产日志。 */
    @Test // 标记为 JUnit 测试方法
    public void test() {
        List<SysUser> list = sysUserService.list();
        System.out.print("111111111");
        list.forEach(System.out::println);
    }

    /** 生成一个 BCrypt 测试哈希，便于准备本地测试数据。 */
    @Test // 标记为 JUnit 测试方法
    void encodePassword() {
        System.out.println(passwordEncoder.encode("123456"));
    }

}

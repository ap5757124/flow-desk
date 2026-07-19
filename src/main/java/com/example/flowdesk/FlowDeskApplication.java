package com.example.flowdesk;

import com.example.flowdesk.common.response.R;
import com.example.flowdesk.system.entity.SysUser;
import com.example.flowdesk.system.service.SysUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FlowDesk 应用启动类。
 *
 * <p>{@code @SpringBootApplication} 会组合组件扫描、自动配置和配置类发现，
 * 因此从这里启动后，Controller、Service、Mapper 等 Spring Bean 都会被加载。</p>
 */
@SpringBootApplication // 标记 Spring Boot 启动类，并启用自动配置和当前包下的组件扫描
public class FlowDeskApplication {

    /**
     * Java 应用入口。Spring Boot 会在这里创建 IoC 容器并启动内置 Web 服务器。
     */
    public static void main(String[] args) {
        SpringApplication.run(FlowDeskApplication.class, args);
    }

}





//请先阅读 project/AGENTS.md、project/docs/00-project-vision.md、project/docs/01-architecture.md。
//之后所有开发都遵循这些文档里的项目定位、架构原则和 AI 工作流程。
//我接下来要实现的功能是：XXX。
//请先做功能设计，不要直接写代码。

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

@SpringBootApplication
@RestController
@RequestMapping
public class FlowDeskApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowDeskApplication.class, args);
    }


    @GetMapping
    public R<Map<String, String>> test() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "name");
        map.put("ms", "ms");
        return R.success(map);
    }
}





//请先阅读 project/AGENTS.md、project/docs/00-project-vision.md、project/docs/01-architecture.md。
//之后所有开发都遵循这些文档里的项目定位、架构原则和 AI 工作流程。
//我接下来要实现的功能是：XXX。
//请先做功能设计，不要直接写代码。
package com.example.flowdesk;

import com.example.flowdesk.system.entity.SysUser;
import com.example.flowdesk.system.service.SysUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class FlowDeskApplicationTests {

    @Test
    void contextLoads() {
    }


    @Autowired
    private SysUserService sysUserService;

    @Test
    public void test() {
        List<SysUser> list = sysUserService.list();
        System.out.print("111111111");
        list.forEach(System.out::println);
    }

}

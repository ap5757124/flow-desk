package com.example.flowdesk.system.mapstruct;

import com.example.flowdesk.system.dto.res.SysUserRes;
import com.example.flowdesk.system.entity.SysUser;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

/** 验证用户实体转换为响应 DTO 后不会泄露密码哈希。 */
class SysUserResStructMapperTest {

    private final SysUserResStructMapper mapper = new SysUserResStructMapperImpl();
    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    /** 序列化后的用户响应中不能出现 password 字段或哈希内容。 */
    @Test // 标记为 JUnit 测试方法
    void shouldNotExposePasswordHash() throws Exception {
        SysUser user = new SysUser();
        user.setId(1);
        user.setTenantId(1);
        user.setUsername("admin");
        user.setPassword("bcrypt-hash");

        SysUserRes response = mapper.toResponse(user);
        String json = objectMapper.writeValueAsString(response);

        assertThat(response.getUsername()).isEqualTo("admin");
        assertThat(json).doesNotContain("password", "bcrypt-hash");
    }
}

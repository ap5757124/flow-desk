package com.example.flowdesk.system.mapstruct;

import com.example.flowdesk.system.dto.res.SysUserRes;
import com.example.flowdesk.system.entity.SysUser;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 用户实体到安全响应 DTO 的转换器。
 * MapStruct 在编译期生成字段复制代码，不使用运行时反射。
 */
@Mapper(componentModel = "spring") // 生成实现类，并把实现类注册为 Spring Bean
public interface SysUserResStructMapper {

    /** 将单个用户实体转换为不含密码的响应 DTO。 */
    SysUserRes toResponse(SysUser sysUser);

    /** 批量转换用户列表；MapStruct 会循环调用单对象转换方法。 */
    List<SysUserRes> toResponseList(List<SysUser> sysUsers);
}

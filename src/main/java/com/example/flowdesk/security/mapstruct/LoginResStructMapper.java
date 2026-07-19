package com.example.flowdesk.security.mapstruct;


import com.example.flowdesk.security.dto.res.LoginRes;
import com.example.flowdesk.system.entity.SysUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 登录响应对象转换器。
 *
 * <p>MapStruct 会在编译期生成实现类，把数据库实体中的安全字段隔离在响应之外。</p>
 */
@Mapper(componentModel = "spring") // 生成转换实现类，并把实现类注册为 Spring Bean
public interface LoginResStructMapper {

    /**
     * 将用户实体转换为登录响应；Token 由 AuthService 在转换完成后设置。
     */
    @Mapping(source = "id", target = "userId") // 实体字段 id 与响应字段 userId 名称不同，显式指定映射
    @Mapping(target = "accessToken", ignore = true) // Access Token 由 AuthService 调用 JwtTokenProvider 生成
    @Mapping(target = "refreshToken", ignore = true) // Refresh Token 由 AuthService 调用 JwtTokenProvider 生成
    LoginRes toLoginRes(SysUser sysUser);

}

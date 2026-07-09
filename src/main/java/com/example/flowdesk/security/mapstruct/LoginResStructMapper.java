package com.example.flowdesk.security.mapstruct;


import com.example.flowdesk.security.dto.res.LoginRes;
import com.example.flowdesk.system.entity.SysUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LoginResStructMapper {

//    LoginResStructMapper Instance = Mappers.getMapper(LoginResStructMapper.class);

    @Mapping(source = "id", target = "userId")
    LoginRes toLoginRes(SysUser sysUser);

}

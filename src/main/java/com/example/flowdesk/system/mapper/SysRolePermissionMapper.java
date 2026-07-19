package com.example.flowdesk.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.flowdesk.system.entity.SysRolePermission;
import org.apache.ibatis.annotations.Mapper;

/** 角色权限关联表数据访问接口。 */
@Mapper // 注册为 MyBatis Mapper，基础 CRUD 由 BaseMapper 提供
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {
}

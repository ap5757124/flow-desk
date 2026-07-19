package com.example.flowdesk.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.flowdesk.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/** 用户角色关联表数据访问接口。 */
@Mapper // 注册为 MyBatis Mapper，基础 CRUD 由 BaseMapper 提供
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}

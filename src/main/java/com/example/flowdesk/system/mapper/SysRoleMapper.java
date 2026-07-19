package com.example.flowdesk.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.flowdesk.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/** 角色表数据访问接口，基础 CRUD 方法由 MyBatis-Plus 的 BaseMapper 提供。 */
@Mapper // 注册为 MyBatis Mapper，Spring 可以注入其代理对象
public interface SysRoleMapper extends BaseMapper<SysRole> {
}

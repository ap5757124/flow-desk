package com.example.flowdesk.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.flowdesk.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表数据访问接口。
 * 继承 BaseMapper 后自动拥有 selectById、insert、updateById 等基础数据库方法。
 */
@Mapper // 注册为 MyBatis Mapper，启动时由框架生成代理实现
public interface SysUserMapper extends BaseMapper<SysUser> {
}

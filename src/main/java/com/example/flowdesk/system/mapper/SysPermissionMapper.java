package com.example.flowdesk.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.flowdesk.system.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 权限表数据访问接口，同时声明 RBAC 权限码关联查询。 */
@Mapper // 注册为 MyBatis Mapper，并关联同 namespace 的 XML SQL
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 查询指定租户内某个用户当前有效的权限码。
     *
     * @param userId 用户主键 ID
     * @param tenantId 用户所属租户 ID
     * @return 去重后的权限码列表
     */
    List<String> listPermissionCodesByUserId(
            @Param("userId") // 将 Java 参数命名为 userId，供 XML 使用 #{userId}
            int userId,
            @Param("tenantId") // 将 Java 参数命名为 tenantId，供 XML 使用 #{tenantId}
            int tenantId
    );
}

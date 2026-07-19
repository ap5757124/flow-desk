package com.example.flowdesk.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** sys_permission 表对应的功能权限实体。 */
@Data // Lombok 生成 getter、setter 和常用 Object 方法
@TableName("sys_permission") // 映射到 sys_permission 表
public class SysPermission {
    /** 权限主键 ID。 */
    private int id ;
    /** 权限所属租户 ID。 */
    private int tenantId ;
    /** 权限展示名称。 */
    private String permissionName ;
    /** 权限编码，例如 system:user:list，代码中的 @PreAuthorize 使用它。 */
    private String permissionCode ;
    /** 权限用途说明。 */
    private String permissionDesc ;
    /** 状态：1 启用，0 禁用；禁用后不再授予 authority。 */
    private byte status ;
    /** 创建时间。 */
    private LocalDateTime createTime ;
    /** 最近更新时间。 */
    private LocalDateTime updateTime ;
    /** 逻辑删除标记：1 已删除，0 未删除。 */
    private byte deleted ;
}

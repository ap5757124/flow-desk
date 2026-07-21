package com.example.flowdesk.system.dto.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/** 角色授权或移除权限接口的请求参数。 */
@Data // Lombok 注解：自动生成 getter、setter、toString 等方法
public class SysRolePermissionReq {

    /** 被授权的角色 ID，必须属于当前租户。 */
    @NotNull(message = "角色ID不能为空") // 参数校验注解：角色 ID 不能缺失
    @Positive(message = "角色ID必须大于0") // 参数校验注解：角色 ID 必须是正数
    private Integer roleId;

    /** 授予角色的权限 ID，必须属于当前租户。 */
    @NotNull(message = "权限ID不能为空") // 参数校验注解：权限 ID 不能缺失
    @Positive(message = "权限ID必须大于0") // 参数校验注解：权限 ID 必须是正数
    private Integer permissionId;
}

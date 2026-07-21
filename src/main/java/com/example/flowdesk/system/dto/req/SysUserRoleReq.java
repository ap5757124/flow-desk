package com.example.flowdesk.system.dto.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/** 用户分配或移除角色接口的请求参数。 */
@Data // Lombok 注解：自动生成 getter、setter、toString 等方法
public class SysUserRoleReq {

    /** 被分配角色的用户 ID，必须属于当前租户。 */
    @NotNull(message = "用户ID不能为空") // 参数校验注解：用户 ID 不能缺失
    @Positive(message = "用户ID必须大于0") // 参数校验注解：用户 ID 必须是正数
    private Integer userId;

    /** 要分配给用户的角色 ID，必须属于当前租户。 */
    @NotNull(message = "角色ID不能为空") // 参数校验注解：角色 ID 不能缺失
    @Positive(message = "角色ID必须大于0") // 参数校验注解：角色 ID 必须是正数
    private Integer roleId;
}

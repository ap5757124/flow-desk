package com.example.flowdesk.system.dto.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/** 逻辑删除角色接口的请求参数。 */
@Data // Lombok 注解：自动生成 getter、setter、toString 等方法
public class SysRoleDeleteReq {

    /** 要逻辑删除的角色 ID。 */
    @NotNull(message = "角色ID不能为空") // 参数校验注解：删除操作必须明确目标角色
    @Positive(message = "角色ID必须大于0") // 参数校验注解：ID 必须是正数
    private Integer id;
}

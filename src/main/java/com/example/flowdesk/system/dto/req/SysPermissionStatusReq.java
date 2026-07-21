package com.example.flowdesk.system.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/** 启用或禁用权限接口的请求参数。 */
@Data // Lombok 注解：自动生成 getter、setter、toString 等方法
public class SysPermissionStatusReq {

    /** 要启用或禁用的权限 ID。 */
    @NotNull(message = "权限ID不能为空") // 参数校验注解：ID 不能缺失
    @Positive(message = "权限ID必须大于0") // 参数校验注解：ID 必须是正数
    private Integer id;

    /** 权限状态：1 启用，0 禁用。 */
    @NotNull(message = "权限状态不能为空") // 参数校验注解：状态必须明确传入
    @Min(value = 0, message = "权限状态只能是0或1") // 参数校验注解：状态最小值为 0
    @Max(value = 1, message = "权限状态只能是0或1") // 参数校验注解：状态最大值为 1
    private Integer status;
}

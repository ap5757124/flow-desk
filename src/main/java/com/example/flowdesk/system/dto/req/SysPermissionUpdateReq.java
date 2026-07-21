package com.example.flowdesk.system.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 修改权限接口的请求参数。 */
@Data // Lombok 注解：自动生成 getter、setter、toString 等方法
public class SysPermissionUpdateReq {

    /** 要修改的权限 ID，只能修改当前租户内未删除的权限。 */
    @NotNull(message = "权限ID不能为空") // 参数校验注解：ID 不能缺失
    @Positive(message = "权限ID必须大于0") // 参数校验注解：ID 必须是正数
    private Integer id;

    /** 修改后的权限展示名称。 */
    @NotBlank(message = "权限名称不能为空") // 参数校验注解：权限名称不能为空或纯空格
    @Size(max = 50, message = "权限名称不能超过50个字符") // 参数校验注解：限制权限名称长度
    private String permissionName;

    /** 修改后的权限编码；同一租户内不能和其他权限重复。 */
    @NotBlank(message = "权限编码不能为空") // 参数校验注解：权限编码不能为空或纯空格
    @Size(max = 100, message = "权限编码不能超过100个字符") // 参数校验注解：限制权限编码长度
    @Pattern(regexp = "^[a-z][a-z0-9-]*(?::[a-z][a-z0-9-]*)+$", message = "权限编码格式示例：system:role:list") // 参数校验注解：约束权限码为 module:resource:action 这类可读格式
    private String permissionCode;

    /** 修改后的权限说明，可为空。 */
    @Size(max = 200, message = "权限描述不能超过200个字符") // 参数校验注解：限制权限描述长度
    private String permissionDesc;
}

package com.example.flowdesk.system.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 新增权限接口的请求参数。 */
@Data // Lombok 注解：自动生成 getter、setter、toString 等方法
public class SysPermissionCreateReq {

    /** 权限展示名称，例如“角色列表”。 */
    @NotBlank(message = "权限名称不能为空") // 参数校验注解：权限名称不能为空或纯空格
    @Size(max = 50, message = "权限名称不能超过50个字符") // 参数校验注解：限制权限名称长度
    private String permissionName;

    /** 权限编码，例如 system:role:list；代码中的 @PreAuthorize 使用它。 */
    @NotBlank(message = "权限编码不能为空") // 参数校验注解：权限编码是授权判断依据，必须填写
    @Size(max = 100, message = "权限编码不能超过100个字符") // 参数校验注解：限制权限编码长度
    @Pattern(regexp = "^[a-z][a-z0-9-]*(?::[a-z][a-z0-9-]*)+$", message = "权限编码格式示例：system:role:list") // 参数校验注解：约束权限码为 module:resource:action 这类可读格式
    private String permissionCode;

    /** 权限用途说明，可为空。 */
    @Size(max = 200, message = "权限描述不能超过200个字符") // 参数校验注解：描述不是必填，但要限制长度
    private String permissionDesc;
}

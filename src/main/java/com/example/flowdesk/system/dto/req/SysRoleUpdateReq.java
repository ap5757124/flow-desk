package com.example.flowdesk.system.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 修改角色接口的请求参数。 */
@Data // Lombok 注解：自动生成 getter、setter、toString 等方法
public class SysRoleUpdateReq {

    /** 要修改的角色 ID，只能修改当前租户内未删除的角色。 */
    @NotNull(message = "角色ID不能为空") // 参数校验注解：包装类型 Integer 必须显式校验非空
    @Positive(message = "角色ID必须大于0") // 参数校验注解：ID 必须是正数
    private Integer id;

    /** 修改后的角色展示名称。 */
    @NotBlank(message = "角色名称不能为空") // 参数校验注解：角色名称不能为空或纯空格
    @Size(max = 50, message = "角色名称不能超过50个字符") // 参数校验注解：限制角色名称长度
    private String roleName;

    /** 修改后的角色编码；同一租户内不能和其他角色重复。 */
    @NotBlank(message = "角色编码不能为空") // 参数校验注解：角色编码不能为空或纯空格
    @Size(max = 50, message = "角色编码不能超过50个字符") // 参数校验注解：限制角色编码长度
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "角色编码只能包含大写字母、数字和下划线，且必须以大写字母开头") // 参数校验注解：约束角色编码格式，便于统一管理和排查
    private String roleCode;

    /** 修改后的角色说明，可为空。 */
    @Size(max = 200, message = "角色描述不能超过200个字符") // 参数校验注解：限制角色描述长度
    private String roleDesc;
}

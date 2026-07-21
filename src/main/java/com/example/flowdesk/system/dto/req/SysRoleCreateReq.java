package com.example.flowdesk.system.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 新增角色接口的请求参数。 */
@Data // Lombok 注解：自动生成 getter、setter、toString 等方法，Controller 才能读取 JSON 反序列化后的字段
public class SysRoleCreateReq {

    /** 角色展示名称，例如“工单管理员”。 */
    @NotBlank(message = "角色名称不能为空") // 参数校验注解：字符串不能为 null、空字符串或纯空格
    @Size(max = 50, message = "角色名称不能超过50个字符") // 参数校验注解：限制数据库字段长度，避免过长内容入库
    private String roleName;

    /** 角色业务编码，例如 TICKET_MANAGER；同一租户内不能重复。 */
    @NotBlank(message = "角色编码不能为空") // 参数校验注解：角色编码是权限分配和排查问题的重要标识，必须填写
    @Size(max = 50, message = "角色编码不能超过50个字符") // 参数校验注解：限制角色编码长度
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "角色编码只能包含大写字母、数字和下划线，且必须以大写字母开头") // 参数校验注解：约束角色编码格式，便于统一管理和排查
    private String roleCode;

    /** 角色用途说明，可为空。 */
    @Size(max = 200, message = "角色描述不能超过200个字符") // 参数校验注解：描述不是必填，但也要限制长度
    private String roleDesc;
}

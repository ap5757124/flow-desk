package com.example.flowdesk.security.context;

import com.example.flowdesk.common.exception.BusinessException;
import com.example.flowdesk.common.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 读取当前登录用户信息的工具类。
 *
 * <p>业务代码应从这里取得 userId、tenantId 等可信信息，不要信任客户端
 * 在请求参数中自行传入的租户 ID。</p>
 */
public final class SecurityUtils {

    /** 工具类不需要创建对象，因此使用私有构造器阻止 new。 */
    private SecurityUtils() {
    }

    /**
     * 从 Spring Security 上下文取得当前登录用户。
     *
     * @return JWT 过滤器放入上下文的 LoginUser
     * @throws BusinessException 当前请求未认证时抛出
     */
    public static LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "用户未登录");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof LoginUser loginUser)) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "用户未登录");
        }

        return loginUser;
    }

    /** 获取当前用户 ID。 */
    public static int getUserId() {
        return getLoginUser().getUserId();
    }

    /** 获取可信的当前租户 ID，是多租户查询的主要入口。 */
    public static int getTenantId() {
        return getLoginUser().getTenantId();
    }

    /** 获取当前登录用户名。 */
    public static String getUsername() {
        return getLoginUser().getUsername();
    }

    /** 获取当前用户所属部门 ID。 */
    public static int getDepartmentId() {
        return getLoginUser().getDepartmentId();
    }
}

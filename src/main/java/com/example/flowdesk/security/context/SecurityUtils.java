package com.example.flowdesk.security.context;

import com.example.flowdesk.common.exception.BusinessException;
import com.example.flowdesk.common.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

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

    public static int getUserId() {
        return getLoginUser().getUserId();
    }

    public static int getTenantId() {
        return getLoginUser().getTenantId();
    }

    public static String getUsername() {
        return getLoginUser().getUsername();
    }

    public static int getDepartmentId() {
        return getLoginUser().getDepartmentId();
    }
}
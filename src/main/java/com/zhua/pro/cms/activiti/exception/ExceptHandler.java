package com.zhua.pro.cms.activiti.exception;

import com.zhua.pro.cms.core.R;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一异常处理类
 */
@RestControllerAdvice
public class ExceptHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExceptHandler.class);

    @ExceptionHandler(ActException.class)
    public R handleKvfException(ActException e) {
        LOGGER.error("zhua-pro error:", e);
        if (e.getErrorCode() == null) {
            return R.error(e.getMsg());
        }
        return R.error(e.getErrorCode(), e.getMsg());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public R handleUnauthorizedExceptionException(UnauthorizedException e) {
        LOGGER.error("zhua-pro error:{}", e.getMessage());
        return R.error(333, "权限不足，请联系管理员。");
    }

    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        LOGGER.error("zhua-pro error:", e);
        return R.error(333, e.getMessage());
    }
}

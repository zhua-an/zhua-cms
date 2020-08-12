package com.zhua.pro.cms.exception;

import com.zhua.pro.cms.core.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

/**
 * 全局异常处理
 *
 * @author zhua
 * @date:
 */
@RestControllerAdvice
public class GlobalExceptionResolver {
    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionResolver.class);


    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public R validatedBindException(BindException e) {
        logger.error("自定义验证异常", e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return R.error(message);
    }


    /**
     * 请求方式不支持
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public R handleException(HttpRequestMethodNotSupportedException e) {
        logger.error("请求方式不支持异常:", e);
        return R.error("不支持' " + e.getMethod() + "'请求");
    }


    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handle(Exception e) {
        logger.error("系统异常:", e);
        ModelAndView mv = new ModelAndView();
        mv.addObject("message", e.getMessage());
        mv.setViewName("/error/999");
        return mv;
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public R notFount(RuntimeException e) {
        logger.error("运行时异常:", e);
        return R.error("运行时异常:" + e.getMessage());
    }

}

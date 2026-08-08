package com.hanshi.weather.common.exception;

import com.hanshi.weather.common.result.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器：统一返回 {code, msg, data} 格式
 *
 * 使用方式：子模块启动类所在包或其父包下，Spring自动扫描 @RestControllerAdvice
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 业务异常 */
    @ExceptionHandler(BusinessException.class)
    public R<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, msg={}", e.getCode(), e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /** 空指针 */
    @ExceptionHandler(NullPointerException.class)
    public R<?> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常", e);
        return R.fail(500, "服务器内部错误：数据异常");
    }

    /** 兜底异常 */
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e) {
        log.error("未知异常", e);
        return R.fail(500, "服务器繁忙，请稍后再试");
    }
}

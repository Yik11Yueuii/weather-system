package com.hanshi.weather.common.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 统一日志拦截器：记录每个请求的URL、参数、耗时、响应码
 *
 * 使用方式：在子模块的 WebMvcConfigurer 中注册
 * <pre>
 * registry.addInterceptor(new LogInterceptor()).addPathPatterns("/**");
 * </pre>
 */
public class LogInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LogInterceptor.class);

    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        START_TIME.set(System.currentTimeMillis());
        String queryString = request.getQueryString();
        String params = queryString != null ? "?" + queryString : "";
        log.info("→ [{}] {}{}  from {}", request.getMethod(), request.getRequestURI(), params, request.getRemoteAddr());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                 Object handler, Exception ex) {
        long cost = System.currentTimeMillis() - START_TIME.get();
        START_TIME.remove();
        int status = response.getStatus();
        if (ex != null) {
            log.error("← [{}] {} → {} ({}ms) 异常: {}", request.getMethod(),
                    request.getRequestURI(), status, cost, ex.getMessage());
        } else {
            log.info("← [{}] {} → {} ({}ms)", request.getMethod(),
                    request.getRequestURI(), status, cost);
        }
    }
}

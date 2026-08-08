package com.hanshi.weather.common.filter;

import com.hanshi.weather.common.constant.WeatherConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 简易Token校验过滤器：防止未授权调用内部接口
 *
 * 内部微服务之间调用需携带请求头 X-Internal-Token
 * 使用方式：在子模块中通过 @Bean 注册 FilterRegistrationBean
 */
public class AuthFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    /** 跳过鉴权的路径前缀 */
    private static final String[] SKIP_PATHS = {
            "/query/weather/cache/",   // 用户端查询（经Zuul过来）
            "/city/list",              // 城市列表（经Zuul过来）
            "/city/",                  // 城市查询（经Zuul过来）
    };

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();

        // 检查是否跳过鉴权
        boolean skip = false;
        for (String path : SKIP_PATHS) {
            if (uri.startsWith(path)) {
                skip = true;
                break;
            }
        }

        if (skip) {
            chain.doFilter(request, response);
            return;
        }

        // 内部接口需要Token
        String token = request.getHeader(WeatherConstant.AUTH_HEADER);
        if (token != null && token.equals(WeatherConstant.INTERNAL_TOKEN)) {
            chain.doFilter(request, response);
        } else {
            log.warn("未授权访问 {} from {}", uri, request.getRemoteAddr());
            response.setStatus(403);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":403,\"msg\":\"Forbidden: 内部接口需要Token\"}");
        }
    }
}

package com.gcc.impossible.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/** X-Host 헤더(imbank/imshop/web)를 읽어 요청 스코프 HostContext 에 채운다 */
public class HostContextInterceptor implements HandlerInterceptor {

    private final HostContext hostContext;

    public HostContextInterceptor(HostContext hostContext) {
        this.hostContext = hostContext;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader("X-Host");
        hostContext.setHost(parse(header));
        return true;
    }

    private Host parse(String header) {
        if (header == null) {
            return Host.UNKNOWN;
        }
        try {
            return Host.valueOf(header.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Host.UNKNOWN;
        }
    }
}

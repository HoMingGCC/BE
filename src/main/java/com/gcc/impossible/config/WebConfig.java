package com.gcc.impossible.config;

import com.gcc.impossible.context.HostContext;
import com.gcc.impossible.context.HostContextInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final HostContext hostContext;

    public WebConfig(HostContext hostContext) {
        this.hostContext = hostContext;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // FE(Vite) 로컬 개발 서버 오리진 허용
        registry.addMapping("/api/**")
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HostContextInterceptor(hostContext)).addPathPatterns("/api/**");
    }
}

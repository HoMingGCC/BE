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
        registry.addMapping("/api/**")
                .allowedOriginPatterns(
                        // FE(Vite) 로컬 개발 서버
                        "http://localhost:*",
                        "http://127.0.0.1:*",
                        // FE 배포 도메인 (Vercel) — 같은 프로젝트의 프리뷰 배포까지 커버
                        "https://fe-6ab3.vercel.app",
                        "https://fe-6ab3-*.vercel.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HostContextInterceptor(hostContext)).addPathPatterns("/api/**");
    }
}

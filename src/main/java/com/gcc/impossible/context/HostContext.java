package com.gcc.impossible.context;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * host 분기 로직의 진입점. 지금은 하드 ACL을 걸지 않고 X-Host 헤더를 요청 스코프로 들고 있다가
 * 각 서비스가 필요할 때 참고하는 확장 지점으로만 쓴다 (과설계 방지).
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class HostContext {

    private Host host = Host.UNKNOWN;

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }
}

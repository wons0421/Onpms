package kr.co.onandon.onpms.jwt;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtEntryPoint extends BasicAuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.addHeader("WWW-Authenticate", "Basic realm=" + super.getRealmName() + "");

        PrintWriter writer = response.getWriter();
        writer.println("HTTP Status 401 - " + request.getAttribute("unauthorization"));
    }

    @Override
    public void afterPropertiesSet() {
        super.setRealmName("onandon");
        super.afterPropertiesSet();
    }
}

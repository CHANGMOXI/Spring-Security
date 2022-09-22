package org.javaboy.jwtdemo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.startup.RealmRuleSet;
import org.javaboy.jwtdemo.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author chenzhisheng
 * @date 2022/09/22 12:58
 **/
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {
    protected JwtLoginFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager) {
        super(defaultFilterProcessesUrl, authenticationManager);
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
    }


}

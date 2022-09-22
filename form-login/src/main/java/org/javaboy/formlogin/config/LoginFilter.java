package org.javaboy.formlogin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenzhisheng
 * @date 2022/09/20 11:33
 **/
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    /**
     * 使用JSON格式登录
     * 自定义过滤器
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        String verify_code = (String) request.getSession().getAttribute("verify_code");
        if (request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE) || request.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE)) {
            //通过JSON传递参数
            Map<String, String> loginData = new HashMap<>();
            try {
                //通过读取request中的I/O流，将 JSON 映射到一个 Map 上
                loginData = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            } catch (IOException e) {
            } finally {
                String code = loginData.get("code");
                checkCode(response, code, verify_code);
            }
            String username = loginData.get(getUsernameParameter());
            String password = loginData.get(getPasswordParameter());
            if (username == null) {
                username = "";
            }
            if (password == null) {
                password = "";
            }
            username = username.trim();
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        } else {
            //不是通过JSON传递参数
            checkCode(response, request.getParameter("code"), verify_code);
            //支持key/value形式传递参数
            return super.attemptAuthentication(request, response);
        }
    }

    public void checkCode(HttpServletResponse response, String code, String verify_code) {
        if (code == null || verify_code == null || "".equals(code) || !verify_code.toLowerCase().equals(code.toLowerCase())) {
            //验证码不正确
            throw new AuthenticationServiceException("验证码不正确");
        }
    }
}

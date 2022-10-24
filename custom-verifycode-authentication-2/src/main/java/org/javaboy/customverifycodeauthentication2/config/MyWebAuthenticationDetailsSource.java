package org.javaboy.customverifycodeauthentication2.config;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义 WebAuthenticationDetailsSource
 *
 * @author chenzhisheng
 * @date 2022/10/24 12:43
 **/
@Component
public class MyWebAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, MyWebAuthenticationDetails> {
    @Override
    public MyWebAuthenticationDetails buildDetails(HttpServletRequest context) {
        //在 MyWebAuthenticationDetailsSource 构造器中 构造 自定义的 MyWebAuthenticationDetails 并返回
        return new MyWebAuthenticationDetails(context);
    }
}

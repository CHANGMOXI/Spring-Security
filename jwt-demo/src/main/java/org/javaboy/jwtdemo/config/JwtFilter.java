package org.javaboy.jwtdemo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * JWT相关的过滤器：
 *      token校验的过滤器JwtFilter
 *
 * @author CZS
 * @create 2022-09-24 23:56
 **/
public class JwtFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        //从请求头中提取authorities字段，对应的value就是用户的token
        String jwtToken = req.getHeader("authorization");
        System.out.println(jwtToken);
        Claims claims = Jwts.parser().setSigningKey("sang@123").parseClaimsJws(jwtToken.replace("Bearer", ""))
                .getBody();
        //获取当前登录用户名
        String username = claims.getSubject();
        //获取当前用户角色
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get("authorities"));
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, authorities);
        //放到当前Context中
        SecurityContextHolder.getContext().setAuthentication(token);
        chain.doFilter(req, response);
    }
}

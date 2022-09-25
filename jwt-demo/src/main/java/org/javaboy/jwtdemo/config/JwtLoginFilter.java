package org.javaboy.jwtdemo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.catalina.startup.RealmRuleSet;
import org.javaboy.jwtdemo.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;

/**
 * JWT相关的过滤器：
 * 用户登录的过滤器JwtLoginFilter
 * 在该过滤器中校验用户是否登录成功
 * 如果成功，则生成一个token返回给客户端，如果失败，则返回一个错误提示给客户端
 *
 * @author chenzhisheng
 * @date 2022/09/22 12:58
 **/
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {
    protected JwtLoginFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager) {
        super(defaultFilterProcessesUrl, authenticationManager);
        setAuthenticationManager(authenticationManager);
    }

    /**
     * 从登录参数中提取出用户名、密码，然后调用 AuthenticationManager.authenticate()方法 进行自动校验
     *
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
    }

    /**
     * 在attemptAuthentication方法中如果校验成功，会回调该方法
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        StringBuffer as = new StringBuffer();
        //遍历用户角色，用","连接起来
        for (GrantedAuthority authority : authorities) {
            as.append(authority.getAuthority()).append(",");
        }

        //利用Jwts生成token
        String jwt = Jwts.builder()
                .claim("authorities", as)//配置用户角色
                .setSubject(authResult.getName())//配置主题
                .setExpiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000))//配置过期时间
                .signWith(SignatureAlgorithm.HS512, "sang@123")//配置加密算法和密钥
                .compact();

        //将token返回给客户端
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(jwt));
        out.flush();
        out.close();
    }

    /**
     * 在attemptAuthentication方法中如果校验失败，会回调该方法
     *
     * @param request
     * @param response
     * @param failed
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        //返回一个错误提示给客户端
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write("登录失败！");
        out.flush();
        out.close();
    }
}

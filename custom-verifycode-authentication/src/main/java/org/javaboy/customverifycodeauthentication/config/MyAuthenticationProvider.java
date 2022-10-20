package org.javaboy.customverifycodeauthentication.config;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义 MyAuthenticationProvider 继承自 DaoAuthenticationProvider
 * 重写 additionalAuthenticationChecks 方法，加入 验证码的校验
 *
 * @author chenzhisheng
 * @date 2022/10/19 18:04
 **/
public class MyAuthenticationProvider extends DaoAuthenticationProvider {
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        //获取当前请求，基于Spring的web项目中，可以随时随地获取到当前请求
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //从当前请求中拿到 code 参数，也就是用户输入后传来的验证码
        String code = request.getParameter("code");
        //从 session 中获取生成的验证码字符串
        String verify_code = (String) request.getSession().getAttribute("verify_code");
        if (code == null || verify_code == null || !code.equals(verify_code)) {
            throw new AuthenticationServiceException("验证码错误");
        }

        //验证码校验完之后，调用父类 additionalAuthenticationChecks 方法，该方法主要做密码的校验
        super.additionalAuthenticationChecks(userDetails, authentication);
    }
}

package org.javaboy.customverifycodeauthentication2.config;

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
 * 重写 additionalAuthenticationChecks 方法，原本的验证码校验放到了{@link MyWebAuthenticationDetails}中
 * 这里只需判断验证码校验成功或失败
 *
 * @author chenzhisheng
 * @date 2022/10/19 18:04
 **/
public class MyAuthenticationProvider extends DaoAuthenticationProvider {
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        //只需获取details里的isPassed属性，获取验证码校验结果
        if (!((MyWebAuthenticationDetails) authentication.getDetails()).isPassed()) {
            throw new AuthenticationServiceException("验证码错误");
        }

        //验证码校验完之后，调用父类 additionalAuthenticationChecks 方法，该方法主要做密码的校验
        super.additionalAuthenticationChecks(userDetails, authentication);
    }
}

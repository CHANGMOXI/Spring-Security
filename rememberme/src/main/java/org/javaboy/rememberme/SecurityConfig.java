package org.javaboy.rememberme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author CZS
 * @create 2022-10-16 16:36
 **/
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("CHANGMOXI")
                .password("123456")
                .roles("admin");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                //令牌: Q0hBTkdNT1hJOjE2NjcxMjEzMDI0NzM6NjQwZGNmMGE4ODA0ZWQxZDUwNmQyMDQ3ODJkN2NhN2M
                //解码后: CHANGMOXI:1667121302473:640dcf0a8804ed1d506d204782d7ca7c
                //第三段的散列值的明文格式: username : tokenExpiryTime : password : key
                //  重新打开浏览器登录，会携带着cookie中的remember-me令牌到服务端
                //  服务端拿到值后，拿到里面的用户名、过期时间，根据用户名查询到密码
                //  再通过 MD5散列函数 计算出散列值(根据username、令牌有效期、password、key计算)
                //  把它和浏览器传过来的令牌的散列值对比，相同则令牌有效
                .rememberMe()
                //key参与到自动登录的验证
                //默认为UUID,弊端是服务端重启后会导致派发出去的所有remember-me自动登录令牌失效
                //可以手动指定key,即使服务端重启,关闭浏览器再打开依然能自动登录
                .key("CHANGMOXI")
                .and()
                .csrf().disable();
    }
}

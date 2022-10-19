package org.javaboy.remembermepersis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * 自动登录存在一定安全风险，两种降低安全风险方案 ---> 方案一: 持久化令牌  方案二: 二次校验
 *      方案一: 持久化令牌
 *              新增了两个经过 MD5 散列函数计算的校验参数，提高系统的安全性
 *              一个是 series，另一个是 token
 *              series 只有当用户在使用用户名/密码登录时，才会生成或者更新
 *              token 只要有新的会话，就会重新生成，这样就可以避免一个用户同时在多端登录
 *      方案二: 二次校验
 * @author CZS
 * @create 2022-10-16 16:36
 **/
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }


    @Resource
    DataSource dataSource;
    /**
     * 持久化令牌① 提供一个 JdbcTokenRepositoryImpl 实例，并给其配置 DataSource 数据源
     */
    @Bean
    JdbcTokenRepositoryImpl jdbcTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
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
                /** 二次校验 接口访问配置 **/
                /** /rememberme接口 需要 rememberMe(自动登录认证) 才能访问 **/
                .antMatchers("/rememberme").rememberMe()
                /** /admin接口 需要 fullyAuthenticated(不包含自动登录认证) 才能访问 **/
                .antMatchers("/admin").fullyAuthenticated()
                /** 其余接口比如/hello 都是 authenticated(用户名密码 或 自动登录) 就能访问 **/
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
                /** 持久化令牌② 通过 tokenRepository 将 JdbcTokenRepositoryImpl实例 纳入配置中 **/
                .tokenRepository(jdbcTokenRepository())
                .and()
                .csrf().disable();
    }
}

package org.javaboy.formlogin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * @author CZS
 * @date 2022-09-18 16:40
 **/

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * 会覆盖配置文件中的 用户名、密码 等配置
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("CHANGMOXI123")
                .password("123456")
                .roles("admin");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/doLogin")
                .usernameParameter("name")
                .passwordParameter("pwd")
                /* 前后端不分离 的 登录成功 回调 */
                //defaultSuccessUrl、successForwardUrl 二选一
//                .defaultSuccessUrl("/index")
                .successForwardUrl("/index")
                /* 前后端不分离 的 登录失败 回调 */
                //failureForwardUrl、failureUrl 二选一
//                .failureForwardUrl("/failure1")
//                .failureUrl("/failure2")
                .permitAll()
                .and()
                /* 注销登录 配置 */
//                .logout()
//                //logoutUrl、logoutRequestMatcher 二选一
////                .logoutUrl("/logout")
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
//                .logoutSuccessUrl("/index")
//                .deleteCookies()
//                .permitAll()
//                .and()
                .csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
    }
}

package org.javaboy.jwtdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
        //简单起见，这里没有连接数据库，直接在内存中配置两个用户，具备不同的角色
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password("123456")
                .roles("admin")
                .and()
                .withUser("sang")
                .password("456789")
                .roles("user");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //配置路径规则，/hello接口必须要具备user角色才能访问
                .antMatchers("/hello").hasRole("user")
                //配置路径规则，/admin接口必须要崛北admin角色才能访问
                .antMatchers("/admin").hasRole("admin")
                //POST请求并且是/login接口 则可以直接通过
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                //其他接口必须认证后才能访问
                .anyRequest().authenticated()
                .and()
                //配置上两个自定义的过滤器
                .addFilterBefore(new JwtLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();
    }

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
//    }
}

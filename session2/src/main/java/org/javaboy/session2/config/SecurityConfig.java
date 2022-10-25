package org.javaboy.session2.config;

import org.javaboy.session2.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.annotation.Resource;

/**
 * 前后端分离，如何踢掉已登录用户
 *
 * @author chenzhisheng
 * @date 2022/10/19 17:55
 **/
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    UserService userService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * 角色继承
     * 需要手动给角色加上 ROLE_ 前缀
     *
     * @return
     */
    @Bean
    RoleHierarchy roleHierarchy() {
        //使用的是 RoleHierarchy接口的实现类
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        //表示 ROLE_admin 自动具备 ROLE_user 的权限
        hierarchy.setHierarchy("ROLE_admin > ROLE_user");
        return hierarchy;
    }

    /**
     * 使用自定义的 UserService 配置用户: 重写 configure(AuthenticationManagerBuilder auth)
     * (不是基于内存，也不是基于JdbcUserDetailsManager，而是基于自定义的 UserService)
     *
     * @param auth the {@link AuthenticationManagerBuilder} to use
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login.html")
                .permitAll()
                .and()
                .csrf().disable()
                .sessionManagement()
                /** 配置最大会话数为1，后面的登录就会自动踢掉前面的登录 **/
                .maximumSessions(1);
                /** 禁止新的登录操作 而不是踢掉前面的登录 **/
//                .maxSessionsPreventsLogin(true);
    }

    /**
     * 默认的session失效是通过调用 StandardSession#invalidate 方法来实现的
     * 但这一个失效事件无法被 Spring 容器感知到，进而导致当用户注销登录之后，Spring Security 没有及时清理会话信息表，以为用户还在线，进而导致用户无法重新登录进来
     * 解决方案: 提供一个 HttpSessionEventPublisher，这个类实现了 HttpSessionListener 接口
     * 在该 Bean 中，可以将 session 创建以及销毁的事件及时感知到，并且调用 Spring 中的事件机制将相关的创建和销毁事件发布出去，进而被 Spring Security 感知到
     *
     * @return
     */
    @Bean
    HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}

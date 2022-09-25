package com.javaboy.formlogin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.io.PrintWriter;

/**
 * Spring Security 授权操作
 *
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
     * 基于内存配置多个用户 方式一：重写 configure(AuthenticationManagerBuilder auth)
     *      会覆盖配置文件中的 用户名、密码 等配置
     * @param auth
     * @throws Exception
     */
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("CHANGMOXI123")
//                .password("123456")
//                .roles("admin")
//                .and()
//                .withUser("长莫西")
//                .password("123")
//                .roles("user");
//    }

    /**
     * 基于内存配置多个用户 方式二：重写 userDetailsService()
     *      会覆盖配置文件中的 用户名、密码 等配置
     *
     * @return
     */
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("CHANGMOXI111").password("123456").roles("admin").build());
        manager.createUser(User.withUsername("长莫西").password("123").roles("user").build());
        return manager;
    }

    /**
     * 角色继承
     *      需要手动给角色加上 ROLE_ 前缀
     * @return
     */
    @Bean
    RoleHierarchy roleHierarchy(){
        //使用的是 RoleHierarchy接口的实现类
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        //表示 ROLE_admin 自动具备 ROLE_user 的权限
        hierarchy.setHierarchy("ROLE_admin > ROLE_user");
        return hierarchy;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //如果请求路径满足 /admin/** 格式，则用户需要具备 admin 角色
                .antMatchers("/admin/**").hasRole("admin")
                //如果请求路径满足 /user/** 格式，则用户需要具备 user 角色
                .antMatchers("/user/**").hasRole("user")
                //其余的其他格式的请求路径，只需要认证（登录）后就可以访问
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginProcessingUrl("/doLogin")
                //****** 登录成功的回调 ******
                //successHandler功能强大，还包括了defaultSuccessUrl 和 successForwardUrl 的功能
                //后面这两个适合前后端不分离的登录成功跳转
                //successHandler方法的参数是 AuthenticationSuccessHandler对象
                //  ---> 要实现该对象中的onAuthenticationSuccess方法，该方法有三个参数：HttpServletRequest、HttpServletResponse、Authentication
                //      ---> 可以进行各种方式的返回数据，比如HttpServletRequest做服务端跳转，HttpServletResponse做客户端跳转，同时也可以返回JSON
                //      ---> 第三个参数 Authentication 则保存了刚刚登录成功的用户信息
                .successHandler((req, resp, authentication) -> {
                    Object principal = authentication.getPrincipal();
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write(new ObjectMapper().writeValueAsString(principal));
                    out.flush();
                    out.close();
                })
                //****** 登录失败的回调 ******
                //同样有failureHandler，实现的方法也是三个参数，前两个一样是HttpServletRequest、HttpServletResponse，第三个是Exception
                //对于登录失败，会有不同的原因，Exception 则保存了登录失败的原因，可以通过 JSON 将 失败原因 返回到前端
                .failureHandler((req, resp, e) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write(e.getMessage());
                    out.flush();
                    out.close();
                })
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler((req, resp, authentication) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write("注销成功");
                    out.flush();
                    out.close();
                })
                .permitAll()
                .and()
                .csrf().disable().exceptionHandling()
                //****** 未认证的处理 ******
                //重写 AuthenticationEntryPoint接口 的 实现类LoginUrlAuthenticationEntryPoint 的 commence方法
                //原本该方法是用来在 未认证时 决定 重定向还是forward，默认是重定向
                //统一用JSON交互的话，就不应该重定向跳转页面，重写这个方法 返回JSON提示 即可
                .authenticationEntryPoint((req, resp, authException) -> {
                            resp.setContentType("application/json;charset=utf-8");
                            PrintWriter out = resp.getWriter();
                            out.write("尚未登录，请先登录");
                            out.flush();
                            out.close();
                        }
                );
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
    }
}

package org.javaboy.customverifycodeauthentication.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.javaboy.customverifycodeauthentication.model.RespBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Properties;

/**
 * 之前自定义过滤器加入验证码校验，并加入到 Spring Security 过滤器链
 * 这种方式的弊端: 破坏了原有过滤器链，每次请求都要走一遍验证码过滤器，实际上只需要登录请求经过该过滤器，其他请求不需要
 * <p>
 * 自定义认证逻辑(验证码) 高级玩法一:
 * 1.自定义 MyAuthenticationProvider(加入验证码校验)，代替 DaoAuthenticationProvider，重写additionalAuthenticationChecks方法 {@link MyAuthenticationProvider}
 * 2.自己提供 ProviderManager，并注入自定义的 MyAuthenticationProvider {@link SecurityConfig#authenticationManager()}
 *
 * @author chenzhisheng
 * @date 2022/10/19 17:55
 **/
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * 提供一个描述验证码基本信息的实体类Bean
     *
     * @return
     */
    @Bean
    Producer verifyCode() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "150");
        properties.setProperty("kaptcha.image.height", "50");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

    /**
     * UserDetailsService实例，这里方便起见将用户直接存在内存
     *
     * @return
     */
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("changmoxi").password("123456").roles("admin").build());
        return manager;
    }

    /**
     * 提供一个 MyAuthenticationProvider 的实例，创建该实例时，需要提供 UserDetailService 和 PasswordEncoder 实例
     *
     * @return
     */
    @Bean
    MyAuthenticationProvider myAuthenticationProvider() {
        MyAuthenticationProvider myAuthenticationProvider = new MyAuthenticationProvider();
        myAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        myAuthenticationProvider.setUserDetailsService(userDetailsService());
        return myAuthenticationProvider;
    }

    /**
     * 自己提供 ProviderManager，并注入自定义的 MyAuthenticationProvider
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        ProviderManager manager = new ProviderManager(Arrays.asList(myAuthenticationProvider()));
        return manager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //验证码接口 任何人都能访问
                .antMatchers("/vc.jpg").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .successHandler((request, response, auth) -> {
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter out = response.getWriter();
                    out.write(new ObjectMapper().writeValueAsString(RespBean.ok("success", auth.getPrincipal())));
                    out.flush();
                    out.close();
                })
                .failureHandler((request, response, e) -> {
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter out = response.getWriter();
                    out.write(new ObjectMapper().writeValueAsString(RespBean.error(e.getMessage())));
                    out.flush();
                    out.close();
                })
                .permitAll()
                .and()
                .csrf().disable();
    }
}

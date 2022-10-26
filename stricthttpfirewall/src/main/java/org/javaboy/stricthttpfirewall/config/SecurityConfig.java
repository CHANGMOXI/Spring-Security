package org.javaboy.stricthttpfirewall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.util.Collection;

/**
 * @author chenzhisheng
 * @date 2022/10/26 11:02
 **/
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * StrictHttpFirewall 默认 只允许 DELETE、GET、HEAD、OPTIONS、PATCH、POST、PUT 请求
     * 如果想发送其他HTTP请求，比如 TRACE，需要自己重新提供一个 StrictHttpFirewall实例
     * 在这个实例中同样可以自定义其他地方
     *
     * @return
     */
    @Bean
    HttpFirewall httpFirewall() {
        StrictHttpFirewall strictHttpFirewall = new StrictHttpFirewall();
        //表示不做 HTTP 请求方法校验，也就是什么请求方法都可以通过
        strictHttpFirewall.setUnsafeAllowAnyHttpMethod(true);
        //也可以通过 setAllowedHttpMethods 方法来重新定义可以通过的请求方法
        /**{@link StrictHttpFirewall#setAllowedHttpMethods(Collection)}**/

        //表示允许请求地址中允许出现 ;
//        strictHttpFirewall.setAllowSemicolon(true);
        return strictHttpFirewall;
    }
}

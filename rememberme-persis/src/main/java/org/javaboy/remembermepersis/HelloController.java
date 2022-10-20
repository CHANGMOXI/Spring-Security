package org.javaboy.remembermepersis;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CZS
 * @create 2022-10-16 16:34
 **/
@RestController
public class HelloController {
    /**
     * /hello接口 只要认证后就可以访问，无论是通过用户名密码认证还是通过自动登录认证，只要认证了，就可以访问
     * @return
     */
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    /**
     * /admin接口 必须要用户名密码认证之后才能访问，如果用户是通过自动登录认证的，则必须重新输入用户名密码才能访问该接口
     * @return
     */
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    /**
     * /rememberme接口 必须是通过自动登录认证后才能访问，如果用户是通过用户名/密码认证的，则无法访问该接口
     * @return
     */
    @GetMapping("/rememberme")
    public String rememberme() {
        return "rememberme";
    }
}

package org.javaboy.jwtdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author chenzhisheng
 * @date 2022/09/22 12:50
 **/
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "hello jwt !";
    }

    @GetMapping("/admin")
    public String admin() {
        return "hello admin !";
    }
}

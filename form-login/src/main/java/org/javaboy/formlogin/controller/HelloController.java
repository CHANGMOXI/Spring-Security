package org.javaboy.formlogin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CZS
 * @date 2022-09-18 14:49
 **/

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello Spring Security";
    }

    @RequestMapping("/hello1")
    public String hello1() {
        return "hello1";
    }

    @RequestMapping("/hello2")
    public String hello2() {
        return "hello2";
    }

    @RequestMapping("/failure1")
    public String failure1() {
        return "failure1";
    }

    @RequestMapping("/failure2")
    public String failure2() {
        return "failure2";
    }
}

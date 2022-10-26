package org.javaboy.stricthttpfirewall.controller;

import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenzhisheng
 * @date 2022/10/26 11:03
 **/
@RestController
public class HelloController {
    @RequestMapping(value = "/hello/{id}")
    public void hello(@PathVariable Integer id, @MatrixVariable String name) {
        System.out.println("id = " + id);
        System.out.println("name = " + name);
    }

    @RequestMapping(value = "/hello")
    public void hello(String param) {
        System.out.println("param = " + param);
    }
}

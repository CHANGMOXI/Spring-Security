package org.javaboy.rememberme;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

@SpringBootTest
class RemembermeApplicationTests {

    @Test
    void contextLoads() throws UnsupportedEncodingException {
        String str = new String(Base64.getDecoder().decode("WSUyRlVEMjAydlE0bGVubXIwNWMlMkJqbkElM0QlM0Q6SXUzVUlEWEJkSkN2ZVBFQkNpbE1RdyUzRCUzRA"), "UTF-8");
        System.out.println("str = " + str);
    }

}

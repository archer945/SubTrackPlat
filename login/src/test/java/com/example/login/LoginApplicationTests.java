package com.example.login;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LoginApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testMainMethod() {
        // 测试main方法
        LoginApplication.main(new String[]{});
    }
}

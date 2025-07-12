package com.defectmanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DefectManagerApplicationTests {

    @Test
    void contextLoads() {
        // 测试Spring上下文是否成功加载
    }
    
    @Test
    void testMain() {
        // 测试主方法能否正常执行
        DefectManagerApplication.main(new String[]{});
    }
}

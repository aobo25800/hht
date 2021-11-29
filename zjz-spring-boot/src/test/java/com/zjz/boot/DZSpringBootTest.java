package com.zjz.boot;

import com.zjz.boot.service.DZHelloWorldService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zjz
 * @date 2021/11/29 16:14
 */
@SpringBootTest
public class DZSpringBootTest {

    @Autowired
    private DZHelloWorldService dzHelloWorldService;

    @Test
    public void dz() {
        dzHelloWorldService.dz();
    }
}

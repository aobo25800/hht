package com.zjz.boot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zjz
 * @date 2021/11/29 16:10
 */
@RestController
public class DZHelloWorldController {

    @GetMapping("/dz")
    public String dz() {
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        return "dz";
    }
}

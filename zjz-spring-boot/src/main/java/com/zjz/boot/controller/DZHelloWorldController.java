package com.zjz.boot.controller;

import com.zjz.boot.commons.DZResponse;
import com.zjz.boot.service.DZHelloWorldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zjz
 * @date 2021/11/29 16:10
 */
@RestController
public class DZHelloWorldController {

    @Autowired
    private DZHelloWorldService dzHelloWorldService;

    @GetMapping("/dz")
    public String dz() {
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        return "dz";
    }

    @GetMapping("/parameter")
    public DZResponse<?> parameterVerification(@Validated Long id) {
        dzHelloWorldService.parameterVerification(id);
        return DZResponse.createSuccess(null);
    }
}

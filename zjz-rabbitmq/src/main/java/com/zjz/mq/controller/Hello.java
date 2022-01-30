package com.zjz.mq.controller;

import com.zjz.mq.service.HelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zjz
 * @date 2022/1/29 13:50
 */
@RestController
@RequestMapping("/hello")
public class Hello {

    @Resource
    private HelloService helloService;

    @GetMapping("/func")
    public String func(@RequestParam("data") String data) {
        return helloService.func(data);
    }
}

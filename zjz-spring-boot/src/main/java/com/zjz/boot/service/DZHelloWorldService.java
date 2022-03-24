package com.zjz.boot.service;

import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

/**
 * @author zjz
 * @date 2021/11/29 16:15
 */
@Service
public class DZHelloWorldService {

    public void dz() {
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }

    public void parameterVerification(@NotNull(message = "id不能为空") Long id) {
        System.out.println("request id is: " + id);
    }

}

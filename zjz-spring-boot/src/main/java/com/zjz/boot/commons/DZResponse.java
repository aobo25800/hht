package com.zjz.boot.commons;

import lombok.Data;

/**
 * @author zjz
 * @date 2022/3/17 14:12
 */
@Data
public class DZResponse<T> {
    private static final String SUCCESS_CODE = "success";

    private T result;

    private Integer resCode;

    private String message;

    public static DZResponse<?> createSuccess(Object result) {
        DZResponse dzResponse = new DZResponse();
        dzResponse.setResult(result);
        return dzResponse;
    }
}

package com.zjz.json;

import cn.hutool.json.JSONUtil;

/**
 * @author com.com.zjz
 * @date 2021/9/8 13:56
 */
public class zjzTest {

    public static void main(String[] args) {
        String evidenceJsonStr = JSONUtil.toJsonStr(null);
        System.out.println(evidenceJsonStr);
    }
}

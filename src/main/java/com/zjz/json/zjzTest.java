package com.zjz.json;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;

import java.util.List;

/**
 * @author com.zjz
 * @date 2021/9/8 13:56
 */
public class zjzTest {

    public static void main(String[] args) {
//        JSONObject jsonObject = new JSONObject("[{\"schoolId\":1,\"param\":{\"term6\":{\"shopNo\":\"asd\"},\"term12\":{\"shopNo\":\"qwe\"},\"term24\":{},\"term36\":{}}},{\"schoolId\":1,\"param\":{\"term6\":{\"shopNo\":\"asd\"},\"term12\":{\"shopNo\":\"qwe\"},\"term24\":{},\"term36\":{}}}]");
//        JSONUtil.toList(jsonObject.toJSONArray("schoolId"), TestBean.class);
        String json = "[{\"schoolId\":1,\"param\":{\"term6\":{\"shopNo\":\"asd\"},\"term12\":{\"shopNo\":\"qwe\"},\"term24\":{},\"term36\":{}}},{\"schoolId\":1,\"param\":{\"term6\":{\"shopNo\":\"asd\"},\"term12\":{\"shopNo\":\"qwe\"},\"term24\":{},\"term36\":{}}}]";
        List<TestBean> lists = JSONUtil.toBean(json, new TypeReference<>() {
        }, false);

        System.out.println(lists);
    }
}

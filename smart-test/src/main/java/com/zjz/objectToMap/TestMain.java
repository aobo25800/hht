package com.zjz.objectToMap;

import cn.hutool.json.JSONUtil;

/**
 * @author com.com.zjz
 * @date 2021/8/30 18:39
 */
public class TestMain {

    public static void main(String[] args) {
        ObjectToMap objectToMap = new ObjectToMap();
        objectToMap.setAge(10);
        objectToMap.setName("com/com.zjz");

        // 对象转map
//        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(objectToMap);
//        System.out.println(stringObjectMap);

        // 对象转json
        System.out.println(JSONUtil.toJsonStr(objectToMap));
    }

}

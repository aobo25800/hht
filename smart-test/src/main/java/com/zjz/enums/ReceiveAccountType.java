package com.zjz.enums;


import java.util.HashMap;
import java.util.Map;

/**
 * @author com.com.zjz
 * @date 2021/9/24 14:58
 */
public enum ReceiveAccountType {

    SMART(1, "123"),
    NAME(2, "234");

    private Integer code;
    private String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    ReceiveAccountType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getValueOf(Integer code) {
        System.out.println("枚举中的静态方法。。");
        for (ReceiveAccountType r : ReceiveAccountType.values()) {
            if (r.getCode() == code) {
                return r.getDesc();
            }
        }
        return null;
    }

    static {
        System.out.println("枚举中的静态代码块。。");
    }

}

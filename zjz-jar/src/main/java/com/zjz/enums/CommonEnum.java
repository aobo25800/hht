package com.zjz.enums;

/**
 * @author zjz
 * @date 2022/7/14 11:13
 */
public enum CommonEnum {

    people("people", "人类"),
    animal("animal", "动物")
    ;

    private String value;
    private String desc;

    CommonEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return this.value;
    }

    public String getDesc() {
        return this.desc;
    }

}

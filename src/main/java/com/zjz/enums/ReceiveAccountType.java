package com.zjz.enums;


import java.util.HashMap;
import java.util.Map;

/**
 * @author com.zjz
 * @date 2021/9/24 14:58
 */
public enum ReceiveAccountType {

    SHOP_LEDGER("shopLedger", "商户二级帐本"),
    BO_HAI_LEDGER("boHaiLedger", "渤海银行公户");

    private String code;

    private String value;

    ReceiveAccountType(String code, String value) {
        System.out.println("构造方法执行了");
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    private static final Map<String, ReceiveAccountType> MAP = new HashMap<>();

    static {
        System.out.println("静态代码块执行了");
        for (ReceiveAccountType type : ReceiveAccountType.values()) {
            MAP.put(type.getCode(), type);
        }
    }

    public static ReceiveAccountType get(String code) {
        return MAP.get(code);
    }


}

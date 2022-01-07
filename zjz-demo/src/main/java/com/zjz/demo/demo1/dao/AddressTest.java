package com.zjz.demo.demo1.dao;

import lombok.Data;

/**
 * @author zjz
 * @date 2021/12/13 10:48
 */
@Data
public class AddressTest {
    private Long id;
    private String name;
    private Long parent_id;
    private Integer type;
    private String code;
    private Integer is_hot;
    private String first_spell;
    private Children children;

    @Data
    public static class Children {
        private Long id;
        private String name;
        private Long parent_id;
        private Integer type;
        private String code;
        private Integer is_hot;
        private String first_spell;
    }
}

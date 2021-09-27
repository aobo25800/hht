package com.zjz.json;

import lombok.Data;
import lombok.ToString;

/**
 * @author com.zjz
 * @date 2021/9/8 12:28
 */
@Data
@ToString
public class TestBean {

    private Integer schoolId;
    private SchoolParam param;

    @Data
    @ToString
    public static class SchoolParam {
        private PayParam term3;

        private PayParam term6;

        private PayParam term12;

        private PayParam term24;

        private PayParam term36;

        @Data
        @ToString
        public static class PayParam {
            private String shopNo;
        }
    }

}

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
        private XydPayParam term3;

        private XydPayParam term6;

        private XydPayParam term12;

        private XydPayParam term24;

        private XydPayParam term36;

        @Data
        @ToString
        public static class XydPayParam {
            private String shopNo;
        }
    }

}

package com.zjz.json;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author zjz
 * @date 2021/9/23 10:50
 */
@Data
public class ObjectTest {
    private Long kindergartenId;
    private Map<String, String> kindergartenParam;
}

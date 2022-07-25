package com.zjz.money;

import com.zjz.constant.CommonConstant;
import com.zjz.enums.CommonEnum;
import com.zjz.service.CommonService;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author com.com.zjz
 * @date 2021/8/19 15:52
 */
public class BigDecimalTest {

    public static void main(String[] args) {

        BigDecimal bigDecimal = new BigDecimal("0");
        System.out.println(bigDecimal);

        System.out.println(CommonConstant.CAR_NAME);
        System.out.println(CommonEnum.people.getDesc());

        CommonService commonService = new CommonService();
        String s = commonService.commonFunc();
        System.out.println("jar package return value: " + s);
    }
}

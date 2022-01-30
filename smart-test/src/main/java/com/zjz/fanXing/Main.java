package com.zjz.fanXing;

import com.zjz.fanXing.vo.ProxyVO;
import com.zjz.fanXing.vo.ReslutVO;
import com.zjz.fanXing.vo.SaleVO;

/**
 * @author zjz
 * @date 2022/1/13 16:32
 */
public class Main {

    public <T> ReslutVO func(T element){
        System.out.println(element);
        String name = element.getClass().getName();
        return null;
    }

    public static void main(String[] args) {
        SaleVO saleVO = new SaleVO();
        ProxyVO proxyVO = new ProxyVO();


        Main main = new Main();
        main.func(saleVO);

    }
}

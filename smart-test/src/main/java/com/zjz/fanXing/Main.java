package com.zjz.fanXing;

import com.zjz.fanXing.vo.Dz;
import com.zjz.fanXing.vo.ProxyVO;
import com.zjz.fanXing.vo.ReslutVO;
import com.zjz.fanXing.vo.SaleVO;

/**
 * @author zjz
 * @date 2022/1/13 16:32
 */
public class Main {

    public static void main(String[] args) {
        SaleVO saleVO = new SaleVO();
        ProxyVO proxyVO = new ProxyVO();

//        Dz dz = new Dz();
//        if (proxyVO.getClass().isInstance(proxyVO)) {
//            System.out.println("this is: " + dz);
//        }

        Main main = new Main();
        main.getMapper(proxyVO);

    }

    public <E> void getMapper(E type) {
        ProxyVO proxyVO = new ProxyVO();
        if (type.getClass().isInstance(ProxyVO.class)) {
            System.out.println("this is: " + type);
        }

        if (type instanceof ProxyVO) {
            System.out.println("1");
        }
    }
}

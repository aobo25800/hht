package com.zjz.design.decorate;

/**
 * @author zjz
 * @date 2022/7/20 16:00
 */
public class MoXing implements Che {

    @Override
    public void lunTai() {
        System.out.println("轮胎");
    }

    @Override
    public void dongLi() {
        System.out.println("发动机");
    }
}

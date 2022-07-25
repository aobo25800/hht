package com.zjz.design.decorate;

/**
 * @author zjz
 * @date 2022/7/20 15:43
 */
public class HuoChe extends ZhuangShiChe {

    HuoChe(Che che) {
        super(che);
    }

    @Override
    public void lunTai() {
        System.out.println("这里用的是火车的轮胎");
        super.lunTai();
    }

    @Override
    public void dongLi() {
        System.out.println("这里用的是柴油发动机");
        super.dongLi();
    }
}

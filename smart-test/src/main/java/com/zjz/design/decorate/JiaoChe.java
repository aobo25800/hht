package com.zjz.design.decorate;

/**
 * @author zjz
 * @date 2022/7/20 15:41
 */
public class JiaoChe extends ZhuangShiChe {

    JiaoChe(Che che) {
        super(che);
    }

    @Override
    public void lunTai() {
        System.out.println("这里使用汽车轮胎");
        super.lunTai();
    }

    @Override
    public void dongLi() {
        System.out.println("用的是汽油发动机");
        super.dongLi();
    }
}

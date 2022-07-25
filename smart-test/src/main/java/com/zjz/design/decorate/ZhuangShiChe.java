package com.zjz.design.decorate;

/**
 * @author zjz
 * @date 2022/7/20 15:50
 */
abstract class ZhuangShiChe implements Che {
    protected final Che che;

    ZhuangShiChe(Che che) {
        this.che = che;
    }

    public void lunTai() {
        che.lunTai();
    }

    public void dongLi() {
        che.dongLi();
    }
}

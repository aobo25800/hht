package com.zjz.design.decorate;

/**
 * @author zjz
 * @date 2022/7/20 15:36
 *
 * 装饰器模式：首先确定所实现的效果，应该有共同点。车都有轮胎、动力系统，那就把这两点单独抽出来，上面的壳子就是不同的型号、不同的类型
 *          轿车的壳子就是一个轿车，火车的壳子就是一个火车
 */
public interface Che {

    void lunTai();

    void dongLi();
}

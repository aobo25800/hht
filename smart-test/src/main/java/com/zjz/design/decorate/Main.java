package com.zjz.design.decorate;

/**
 * @author zjz
 * @date 2022/7/20 15:56
 */
public class Main {

    public static void main(String[] args) {
        Che che = new MoXing();
        che.lunTai();
        che.dongLi();
        System.out.println("===================");
        HuoChe huoChe = new HuoChe(che);
        huoChe.lunTai();
        huoChe.dongLi();
        System.out.println("===================");
        JiaoChe jiaoChe = new JiaoChe(che);
        jiaoChe.lunTai();
        jiaoChe.dongLi();
        System.out.println("===================");
    }
}

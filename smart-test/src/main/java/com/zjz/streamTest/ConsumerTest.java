package com.zjz.streamTest;

import cn.hutool.json.JSONUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author zjz
 * @date 2021/10/27 17:22
 */
public class ConsumerTest {

    public static void main(String[] args) {
        List<Person> lisiList = new ArrayList<>();
        // 就是一个匿名函数
        Consumer<Person> consumer  = x -> {
            if (x.name.equals("lisi")){
                lisiList.add(x);
            }
        };
        Stream.of(
                new Person(21,"zhangsan"),
                new Person(22,"lisi"),
                new Person(23,"wangwu"),
                new Person(24,"wangwu"),
                new Person(23,"lisi"),
                new Person(26,"lisi"),
                new Person(26,"zhangsan")
        ).forEach(consumer);

        System.out.println(JSONUtil.toJsonStr(lisiList));
    }

    public static class Person {
        private Integer age;
        private String name;

        Person(){}

        Person(Integer age, String name){
            this.age = age;
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

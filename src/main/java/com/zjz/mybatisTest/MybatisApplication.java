package com.zjz.mybatisTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author zjz
 * @date 2021/9/27 10:10
 */
public class MybatisApplication {

    private String name;

    private Integer age;


    public String getName() {
        return name;
    }

    @Declared(value = "zjz")
    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        MybatisApplication mybatisApplication = new MybatisApplication();
        Class<? extends MybatisApplication> aClass = mybatisApplication.getClass();
        Class<?> aClass1 = Class.forName("com.zjz.mybatisTest.MybatisApplication");
        Class<MybatisApplication> aClass2 = MybatisApplication.class;

        Method[] methods = aClass2.getDeclaredMethods();

        MybatisApplication mybatisApplication1 = aClass2.getDeclaredConstructor().newInstance();

        for (Method m : methods) {
            System.out.println(m.getName());
            if (m.isAnnotationPresent(Declared.class)){
                Declared annotation = m.getAnnotation(Declared.class);
                String value = annotation.value();
                m.invoke(mybatisApplication1, value);
            }
            System.out.println(mybatisApplication1.getName());
        }
    }
}

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@interface Declared{
    String value() default "赵建志";
}

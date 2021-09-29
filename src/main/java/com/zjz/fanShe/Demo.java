package com.zjz.fanShe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zjz
 * @date 2021/9/28 12:10
 */
public class Demo {
    private String name;

    private Integer age;


    public String getName() {
        return name;
    }

    @Declared()
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
        Demo mybatisApplication = new Demo();
        Class<? extends Demo> aClass = mybatisApplication.getClass();
        Class<?> aClass1 = Class.forName("com.zjz.fanShe.Demo");
        Class<Demo> aClass2 = Demo.class;

        Method[] methods = aClass2.getDeclaredMethods();

        Demo Demo1 = aClass2.getDeclaredConstructor().newInstance();

        for (Method m : methods) {
            System.out.println(m.getName());
            if (m.isAnnotationPresent(Declared.class)){
                Declared annotation = m.getAnnotation(Declared.class);
                String value = annotation.value();
                m.invoke(Demo1, value);
            }
            System.out.println(Demo1.getName());
        }
    }
}

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@interface Declared{
    String value() default "赵建志";
}
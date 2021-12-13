package com.zjz.demo.demo3;

/**
 * @author zjz
 * @date 2021/11/17 18:08
 */
public class TestBuilder {
    private Long id;
    private String name;
    private Integer age;
    private String A;
    private String B;

    TestBuilder(Builder builder){
        this.id  = builder.id;
        this.name = builder.name;
        this.age = builder.age;
        this.A = builder.A;
        this.B = builder.B;
    }

    public static class Builder {
        private Long id;
        private String name;
        private Integer age;
        private String A;
        private String B;

        public Builder() {

        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder age(Integer age) {
            this.age = age;
            return this;
        }

        public Builder A(String A) {
            this.A = A;
            return this;
        }

        public Builder B(String B) {
            this.B = B;
            return this;
        }

        public TestBuilder build() {
            return new TestBuilder(this);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getA() {
        return A;
    }

    public void setA(String a) {
        A = a;
    }

    public String getB() {
        return B;
    }

    public void setB(String b) {
        B = b;
    }
}

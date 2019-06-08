package com.isaac.java8.part3.chapter10;

import lombok.Getter;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class OptionalSample {
    @Test
    public void test() {
        Person person = new Person();
        Assert.assertEquals("Unknown", getCarInsuranceName1(person));

        //如果person年龄大于等于15获取保险公司名字的测试
        String result = getCarInsuranceName(Optional.of(person), 15);
        Assert.assertEquals("NoResult", result);
        Insurance insurance = new Insurance();
        insurance.name="Test";
        Car car = new Car();
        car.insurance = insurance;
        person.car = car;
        result = getCarInsuranceName(Optional.of(person), 15);
        Assert.assertEquals("NoResult", result);
        person.age = 25;
        result = getCarInsuranceName(Optional.of(person), 15);
        Assert.assertEquals("Test", result);


    }

    /**
     * 使用Optional获取
     */
    public String getCarInsuranceName1(Person person) {
        Optional<Person> optPerson = Optional.of(person);
        return optPerson.flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                //.orElseThrow(()->new RuntimeException("Insurance name is null"));
                //.orElseGet(()->"Unknow");
                .orElse("Unknown");
    }

    /**
     * 两个Optional对象组合
     */
    public Insurance findCheapestInsurance(Person person, Car car) {
        //不同的保险公司提供的查询服务
        //对比数据
        //return cheapestCompany
        return new Insurance();
    }

    public Optional<Insurance> nullSafeFindCheapestInsurance(Optional<Person> person, Optional<Car> car) {
        return person.flatMap(p -> car.map(c -> findCheapestInsurance(p, c)));
//        if (person.isPresent() && car.isPresent()) {
//            return Optional.of(findCheapestInsurance(person.get(), car.get()));
//        } else {
//            return Optional.empty();
//        }
    }

    /**
     * 使用filter剔除指定的值
     */
    public void ObjCondition(Insurance insurance) {
        if (insurance != null && "CambridgeInsurance".equals(insurance.getName())) {
            //some operation
        }
        Optional<Insurance> optInsurance = Optional.of(insurance);
        optInsurance.filter(insurance1 -> "CambridgeInsurance".equals(insurance1.getName()))
                .ifPresent(x -> {
                });
    }

    /**
     * 如果年龄大于等于minAge参数的Person
     * 返回对应的保险公司名字
     */
    public String getCarInsuranceName(Optional<Person> person, int minAge) {
        return person.filter(p -> p.getAge() >= minAge).flatMap(Person::getCar).flatMap(Car::getInsurance).map(Insurance::getName)
                .orElse("NoResult");
    }

    private class Person {
        @Getter
        private int age;
        @Setter
        private Car car;

        public Optional<Car> getCar() {
            return Optional.ofNullable(car);
        }
    }

    private class Car {
        private Insurance insurance;

        public Optional<Insurance> getInsurance() {
            return Optional.ofNullable(insurance);
        }
    }

    /**
     * insurance公司的名称被声明成String类型，而不是Optional<String>，这非常清楚地表明声明为insurance公司的类型必须提供公司名称。使用
     * 这种方式，一旦解引用insurance公司名称发生NullPointerException，就能非常确定地直到出错的原因，不再需要为其添加null的检查，因为null
     * 的检查只会掩盖问题，并未真正地修复问题。insurance公司必须有个名字，所有，如果遇到一个公司没有名称，需要调查数据除了什么问题，而不应该
     * 再添加一段代码，将这个问题掩藏。
     */
    private class Insurance {
        @Getter
        private String name;
    }
}

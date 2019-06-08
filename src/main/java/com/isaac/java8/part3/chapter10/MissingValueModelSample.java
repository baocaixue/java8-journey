package com.isaac.java8.part3.chapter10;

import lombok.Getter;
import org.junit.Test;

/**
 * 为缺失的值建模
 */
public class MissingValueModelSample {
    @Test public void test(){

    }

    /**
     * 采用防御式检查减少NullPointerException
     */
    public String getCarInsuranceName1(Person person) {
        if(person != null) {
            Car car = person.getCar();
            if (car != null) {
                Insurance insurance = car.getInsurance();
                if (insurance != null) {
                    return insurance.getName();
                }
            }
        }
        return "Unknown";
    }

    /**
     * 过多的退出语句
     */
    public String getCarInsuranceName2(Person person) {
        if (person == null) {
            return "Unknown";
        }
        Car car = person.getCar();
        if (car == null) {
            return "Unknown";
        }
        Insurance insurance = car.getInsurance();
        if (insurance == null) {
            return "Unknown";
        }
        return insurance.getName();
    }

    private class Person {
        @Getter private Car car;
    }

    private class Car {
        @Getter private Insurance insurance;
    }

    private class Insurance {
        @Getter private String name;
    }
}


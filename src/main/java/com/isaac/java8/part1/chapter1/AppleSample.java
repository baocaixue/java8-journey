package com.isaac.java8.part1.chapter1;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class AppleSample {
    /**
     * before java8
     */
    public static List<Apple> filterGreenApplesBefore(List<Apple> inventory) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if ("green".equals(apple.getColor())) {
                result.add(apple);
            }
        }
        return result;
    }

    public static List<Apple> filterHeavyApplesBefore(List<Apple> inventory) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (apple.getWeight() > 150) {
                result.add(apple);
            }
        }
        return result;
    }

    /**
     * java8
     */
    public static List<Apple> filterApples(List<Apple> inventory, Predicate<Apple> p) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (p.test(apple)) result.add(apple);
        }
        return result;
    }

    public static void main(String[] args) {
        List<Apple> inventory = new ArrayList<>();
        AppleSample.filterApples(inventory, AppleSample::isGreenApple);
        AppleSample.filterApples(inventory, AppleSample::isHeavyApple);

        //lambda——代码更干净、清晰，并不需要方法引用那样定义方法。
        //但是这里是方法内容比较简单，所有用lambda更清晰，如果方法复杂还是用方法引用比较好
        AppleSample.filterApples(inventory, apple -> "green".equals(apple));

//        inventory.stream().filter(apple -> "green".equals(apple.getColor())).collect(Collectors.groupingBy(Apple::getWeight));
    }

    private static boolean isGreenApple(Apple apple) {
        return "green".equals(apple.getColor());
    }

    private static boolean isHeavyApple(Apple apple) {
        return apple.getWeight() > 150;
    }

    @Data
    public static class Apple {
        private String color;
        private Integer weight;

    }
}

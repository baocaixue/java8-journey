package com.isaac.java8.part2.chapter4;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class DishSample {
    private static final List<Dish> menu = new ArrayList<>();

    @Before
    public void fillMenu() {
        OwnerSupplier<String, Boolean, Integer, Type, Dish> supplier = Dish::new;
        menu.add(supplier.get("pork", false, 800, Type.MEAT));
        menu.add(supplier.get("beef", false, 700, Type.MEAT));
        menu.add(supplier.get("chicken", false, 400, Type.MEAT));
        menu.add(supplier.get("french fries", true, 530, Type.OTHER));
        menu.add(supplier.get("rice", true, 350, Type.OTHER));
        menu.add(supplier.get("season fruit", true, 120, Type.OTHER));
        menu.add(supplier.get("pizza", true, 550, Type.OTHER));
        menu.add(supplier.get("prawns", false, 300, Type.FISH));
        menu.add(supplier.get("salmon", false, 450, Type.FISH));

    }

    @Test
    public void testGetLowSortedCaloriesDish() {
        List<String> lowCaloriesDishesName1 = getLowSortedCaloriesDishBeforeJava8(menu);
        assertTrue(lowCaloriesDishesName1.size() == 3);
        //use java8
        List<String> lowCaloriesDishesName2 = menu.stream().filter(d -> d.getCalories() < 400)
                .sorted(Comparator.comparing(Dish::getCalories)).map(Dish::getName).collect(Collectors.toList());
        assertTrue(lowCaloriesDishesName2.size() == 3);
    }

    @Test
    public void testStreamProcessFlow() {
        //filter map成对输出，filter和map两个独立操作合并到同一次遍历了——循环合并
        //只有3个 limit操作和一种称为短路的操作
        List<String> names = menu.stream()
                .filter(d -> {
                    System.out.println("filter " + d.getName());
                    return d.getCalories() > 300;
                })
                .map(d -> {
                    System.out.println("map " + d.getName());
                    return d.getName();
                })
                .limit(3)
                .collect(Collectors.toList());
        System.out.println(names);
    }

    private List<String> getLowSortedCaloriesDishBeforeJava8(List<Dish> menu) {
        List<Dish> lowCaloriesDishes = new ArrayList<>();
        List<String> lowCaloriesDishesName = new ArrayList<>();
        for (Dish dish : menu) {
            if (dish.getCalories() < 400)
                lowCaloriesDishes.add(dish);
        }
        Collections.sort(lowCaloriesDishes, new Comparator<Dish>() {
            @Override
            public int compare(Dish o1, Dish o2) {
                return Integer.compare(o1.getCalories(), o2.getCalories());
            }
        });
        for (Dish dish : lowCaloriesDishes) {
            lowCaloriesDishesName.add(dish.getName());
        }
        return lowCaloriesDishesName;
    }

    @Data
    @AllArgsConstructor
    private static class Dish {
        private String name;
        private Boolean vegetarian;
        private Integer calories;
        private Type type;
    }

    private enum Type {MEAT, FISH, OTHER}

    private interface OwnerSupplier<T, T1, T2, T3, R> {
        R get(T name, T1 vegetarian, T2 calories, T3 type);
    }
}

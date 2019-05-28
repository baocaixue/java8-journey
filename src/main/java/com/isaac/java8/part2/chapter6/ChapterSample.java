package com.isaac.java8.part2.chapter6;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;
import static org.junit.Assert.*;

public class ChapterSample {
    private final List<Dish> menu = new ArrayList<>();

    @Before
    public void fillMenu() {
        OwnerSupplier<String, Boolean, Integer, Type, Dish> supplier = Dish::new;
        menu.add(supplier.get("pork", false, 800, Type.MEAT));
        //menu.add(supplier.get("beef", false, 700, Type.MEAT));
        menu.add(supplier.get("chicken", false, 400, Type.MEAT));
        menu.add(supplier.get("french fries", true, 530, Type.OTHER));
        menu.add(supplier.get("rice", false, 350, Type.OTHER));
        menu.add(supplier.get("season fruit", false, 120, Type.OTHER));
        menu.add(supplier.get("pizza", false, 550, Type.OTHER));
        menu.add(supplier.get("prawns", false, 300, Type.FISH));
        menu.add(supplier.get("salmon", false, 450, Type.FISH));
        menu.add(supplier.get("noodles", false, 133, Type.OTHER));
    }

    /**
     * 归约和汇总
     */
    @Test
    public void testReductionAnSummary() {
        /*归约*/
        //从Collectors工厂类中能创建多少种收集器实例
        Long countDishes = menu.stream().collect(counting());
        assertEquals(menu.stream().count(), countDishes.longValue());

        //查找流中的最值
        Comparator<Dish> dishComparator = Comparator.comparing(Dish::getCalories);
        Optional<Dish> maxColoriesDishOptional = menu.stream().collect(maxBy(dishComparator));
        Dish maxColoriesDish = maxColoriesDishOptional.orElse(null);
        assertNotNull(maxColoriesDish);
        assertEquals("pork", maxColoriesDish.getName());

        /*汇总*/
        //long sum1 = menu.stream().mapToLong(Dish::getCalories).sum();
        Integer sum = menu.stream().collect(summingInt(Dish::getCalories));
        assertEquals(3633, sum.longValue());
        //assertEquals(3633, sum1);

        LongSummaryStatistics menuStatistics = menu.stream().collect(summarizingLong(Dish::getCalories));
        assertEquals(800, menuStatistics.getMax());
        assertEquals(9, menuStatistics.getCount());

        /*连接字符串*/
        String concatNames = menu.stream().map(Dish::getName).collect(joining(","));
        assertNotNull(concatNames);

        /* 广义的归约汇总*/
        //所有的收集器，可以用reducing工厂方法是所有这些的一般化
        menu.stream().collect(reducing(0, Dish::getCalories, (i, j) -> i + j));
        //System.out.println(menu.stream().collect(reducing((a, b) -> a)).get());

    }

    /**
     * 分组
     */
    @Test
    public void testGroupBy() {
        //菜单分组，有肉的一组，鱼的一组，其他的一组
        Map<Type, List<Dish>> dishesByType = menu.stream().collect(groupingBy(Dish::getType));//Function分类函数
        assertNotEquals(0, dishesByType.size());

        /* 多级分组*/
        Map<String, Map<String, List<Integer>>> result = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9).stream()
                .collect(groupingBy(number -> {
                    if (number.compareTo(3) <= 0) {
                        return "level1";
                    } else if (number.compareTo(6) <= 0) {
                        return "level2";
                    } else {
                        return "level3";
                    }
                }, groupingBy(n -> {
                    if (n % 2 == 0) {
                        return "偶数";
                    } else {
                        return "奇数";
                    }
                })));
        assertEquals(3, result.size());

        Map<Type, Map<String, List<Dish>>> dishesByTypeCaloricLevel = menu.stream().collect(
                groupingBy(Dish::getType,
                        groupingBy(dish -> {
                            if (dish.getCalories() <= 400) {
                                return "低卡路里";
                            } else {
                                return "高卡路里";
                            }
                        }))
        );
        assertEquals(3, dishesByTypeCaloricLevel.size());

        /*子组收集数据*/
        //把收集器的结果转换为另一种类型
        Map<Type, Optional<Dish>> mostCaloricByType1 = menu.stream()
                .collect(groupingBy(Dish::getType, maxBy(Comparator.comparingInt(Dish::getCalories))));
        //去掉Optional
        //collectingAndThen接受两个参数——要转换的收集器及转换的函数
        Map<Type, Dish> mostCaloricByType2 = menu.stream()
                .collect(
                        groupingBy(Dish::getType, collectingAndThen(maxBy(Comparator.comparingInt(Dish::getCalories)), Optional::get))
                );
        assertEquals(mostCaloricByType1.size(), mostCaloricByType2.size());

        //与groupingBy联合使用的其他收集器
        Map<Type, Integer> totalCaloriesByType = menu.stream().collect(groupingBy(Dish::getType, summingInt(Dish::getCalories)));
        System.out.println(totalCaloriesByType);
        //mapping
        Map<Type, Set<String>> caloricLevelsByType = menu.stream().collect(groupingBy(Dish::getType, mapping(dish -> {
            if (dish.getCalories() <= 400) {
                return "低卡路里";
            } else {
                return "高卡路里";
            }
        }, toSet())));
        System.out.println(caloricLevelsByType);
    }

    /**
     * 分区
     * 分区是分组的特殊情况：由一个谓词作为分类函数，它称为分区函数。分区函数返回布尔值，这意味着得到的分组Map的键类型是Boolean，所以它最多可
     * 以分为两组
     */
    @Test public void testPartition() {
        Map<Boolean, List<Dish>> partitionedMenu = menu.stream().collect(partitioningBy(Dish::getVegetarian));
        assertEquals(2, partitionedMenu.size());
        /*分区优势：保留了分区函数返回true或false的两套流元素列表*/
        Map<Boolean, Dish> mostCaloricPartitionedByVegetarian = menu.stream().collect(partitioningBy(Dish::getVegetarian, collectingAndThen(maxBy(Comparator.comparingInt(Dish::getCalories)), Optional::get)));
        System.out.println(mostCaloricPartitionedByVegetarian);

        /*将数字按质数和非质数分区*/
        IntStream.rangeClosed(2, 100).boxed()
                .collect(partitioningBy(this::isPrime));
    }

    /**
     * 判断一个数是不是质数
     * @param candidate
     * @return
     */
    public boolean isPrime(int candidate){
        int candidateRoot = (int) Math.sqrt((double)candidate);
        return IntStream.rangeClosed(2, candidateRoot).noneMatch(i -> candidate % i == 0);
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

package com.isaac.java8.part2.chapter5;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * 使用流
 */
public class StreamUsingSample {
    private final List<Dish> menu = new ArrayList<>();

    @Before
    public void fillMenu() {
        OwnerSupplier<String, Boolean, Integer, Type, Dish> supplier = Dish::new;
        menu.add(supplier.get("pork", false, 800, Type.MEAT));
        menu.add(supplier.get("beef", false, 700, Type.MEAT));
        menu.add(supplier.get("chicken", false, 400, Type.MEAT));
        menu.add(supplier.get("french fries", true, 530, Type.OTHER));
        menu.add(supplier.get("rice", false, 350, Type.OTHER));
        menu.add(supplier.get("season fruit", false, 120, Type.OTHER));
        menu.add(supplier.get("pizza", false, 550, Type.OTHER));
        menu.add(supplier.get("prawns", false, 300, Type.FISH));
        menu.add(supplier.get("salmon", false, 450, Type.FISH));
        menu.add(supplier.get("noodles", false, 133, Type.OTHER));
    }

    @Test
    public void testStreamFilter() {
        //用谓词筛选
        menu.stream()
                .filter(Dish::getVegetarian)
                .forEach(System.out::println);
    }

    @Test
    public void testStreamDistinct() {
        List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
        numbers.stream()
                .filter(num -> num % 2 == 0)
                .distinct()
                .forEach(System.out::println);
    }

    @Test
    public void testStreamLimit() {
        menu.stream()
                .filter(d -> d.getCalories() > 300)
                .limit(3)
                .forEach(System.out::println);
    }

    @Test
    public void testStreamSkip() {
        //skip(n)方法，返回一个扔掉了前n个元素的流。与limit互补
        List<Dish> dishes = menu.stream()
                .filter(d -> d.getCalories() > 300)
                .skip(2)
                .collect(Collectors.toList());
        dishes.forEach(System.out::println);
    }

    /**
     * 一个非常常见的数据处理套路就是从某些对象中选择信息。比如SQL里，可以从表中选择一列。
     * Stream API通过map和flatMap方法提供了类似的工具
     */
    @Test
    public void testStreamMap() {
        //流支持map方法，他会接受一个函数作为参数。这个函数会被应用到每个元素上，并将其映射成一个新的元素
        /* 给定一个单词列表，返回新的列表，显示每个单词的长度*/
        List<String> words = Arrays.asList("Java8", "Lambda", "In", "Action");
        List<Integer> wordLengths = words.stream()
                .map(String::length)
                .collect(Collectors.toList());
        wordLengths.forEach(System.out::println);
    }

    /**
     * 流的扁平化
     * 例子：给定单词列表，["Hello", "World"]返回列表["H", "e", "l", "o", "W", "r", "d"]
     */
    @Test
    public void testStreamFlatMap() {
        List<String> words = Arrays.asList("Hello", "World");
        //wrong operation
        List<String[]> wrong1 = words.stream()
                .map(item -> item.split(""))//注意这里得到了两个字符串数组的流Stream<String[]>
                .distinct()
                .collect(Collectors.toList());
        assertEquals(2, wrong1.size());

        //wrong 2
        List<Stream<String>> wrong2 = words.stream()
                .map(item -> item.split(""))
                .map(Arrays::stream)//注意这里得到的是Stream<String>
                .distinct()
                .collect(Collectors.toList());
        assertEquals(2, wrong2.size());

        //flatMap
        /*
         *使用flatMap的效果是，各个数组并不是分别映射成一个流，而是映射成流的内容
         *所有使用map(Arrays::stream)时生成的单个流都被合并起来，即扁平化为一个流
         *
         * flatMap让一个流中的每个值都换成另一个流，然后把所有流连接起来成为一个流
         * */
        List<String> uniqueCharacters = words.stream()
                .map(item -> item.split(""))
                .flatMap(Arrays::stream)
                .distinct()
                .collect(Collectors.toList());
        assertEquals(7, uniqueCharacters.size());

        //practice1：给定[1,2,3,4,5]-平方->[1,4,9,16,25]
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> collect = numbers.stream()
                .map(num -> num * num)
                .collect(Collectors.toList());
        assertEquals(25, collect.get(4).longValue());

        //practice2：给定两个数字列表，返回所有数对
        //[1,2,3]和[3,4]-->[(1,3),(1,4),(2,3),(2,4),(3,3),(3,4)]
        List<Integer> numbers1 = Arrays.asList(1, 2, 3);
        List<Integer> number2 = Arrays.asList(3, 4);
        List<int[]> collect1 = numbers1.stream()
                .flatMap(num1 -> number2.stream().map(num2 -> new int[]{num1, num2}))
                .collect(Collectors.toList());
        assertEquals(6, collect1.size());
    }

    @Test
    public void testStreamMatch() {
        /*检查谓词是否至少匹配一个元素*/
        boolean hasVegetarian = menu.stream().anyMatch(Dish::getVegetarian);
        assertTrue(hasVegetarian);

        /*检查谓词是否匹配所有元素（无元素匹配）*/
        boolean normalCalories = menu.stream().allMatch(dish -> dish.getCalories() < 1000);
        assertTrue(normalCalories);
        boolean highCalories = menu.stream().noneMatch(dish -> dish.getCalories() > 1000);
        assertTrue(highCalories);

        /*查找元素*/
        Optional<Dish> anyVegetarian = menu.stream().filter(Dish::getVegetarian).findAny();
        assertTrue(anyVegetarian.isPresent());
        List<Integer> numbers = Arrays.asList(3, 6, 9);
        Optional<Integer> first = numbers.stream().map(num -> num * num).filter(num -> num % 3 == 0).findFirst();
        assertEquals(9, first.orElse(0).longValue());

    }

    /**
     * 归约操作
     */
    @Test
    public void testStreamReduce() {
        /*
         * int sum = 0;
         * for (int x : numbers) sum += x;
         * */
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Integer result1 = numbers.stream().reduce(0, Integer::sum);
        assertEquals(45, result1.longValue());
        //reduce 接受无初始值的lambda，它返回一个Optional

        //find max
        Optional<Integer> maxVal = numbers.stream().reduce(Integer::max);
        assertEquals(9, maxVal.orElse(0).longValue());

        //用map和reduce方法数一数流中有多少个菜
        Integer countDish = menu.stream().map(d -> 1).reduce(0, Integer::sum);
        assertEquals(10, countDish.longValue());
        assertEquals(10, (long) menu.size());
    }

    @Test
    public void testPrimitiveTypeStream() {
        /* 映射到数值流 */
        int sum = menu.stream()
                .mapToInt(Dish::getCalories)
                .sum();
        assert sum == 4333;

        /*转换为对象流*/
        IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
        Stream<Integer> stream = intStream.boxed();
        assertNotNull(stream);

        /*默认OptionalInt*/
        //IntStream求和的时候有默认值0，但是如果计算最大元素，0是个错误的结果。
        //如何区分没有元素流和最大值真的是0的流？
        //对于三种原始流特化，也分别有一个Optional原始类型特化版本：OptionalInt、OptionalDouble和OptionalLong
        OptionalInt maxCalories = menu.stream()
                .mapToInt(Dish::getCalories)
                .max();
        assertTrue(maxCalories.isPresent());

        /*数值范围*/
        long evenCount = IntStream.rangeClosed(1, 100)//包含结束值
                .filter(i -> i % 2 == 0)
                .count();
        assertEquals(50, evenCount);
    }

    /**
     * 数值流应用：勾股数
     */
    @Test
    public void testPrimitiveTypeStreamSpec() {
        Stream<double[]> pythagoreanTriples = IntStream.rangeClosed(1, 100)
                .boxed()
                .flatMap(a ->
                        IntStream.rangeClosed(a, 100)
                                //.filter(b -> Math.sqrt(a * a + b * b) % 1 == 0)
                                .mapToObj(b -> new double[]{a, b, Math.sqrt(a * a + b * b)})
                                .filter(t -> t[2] % 1 == 0)
                );
        pythagoreanTriples.limit(5).forEach(t -> System.out.println(t[0] + "," + t[1] + "," + t[2]));
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

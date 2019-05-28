package com.isaac.java8.part1.chapter1;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CPU利用，并行处理的问题
 * 执行时元素之间无互动
 */
public class StreamSample {

    public static void main(String[] args){
        List<AppleSample.Apple> inventory = new ArrayList<>();
        //顺序处理
        List<AppleSample.Apple> collect = inventory.stream().filter(apple -> apple.getWeight() > 150).collect(Collectors.toList());
        //并行处理
        List<AppleSample.Apple> collect1 = inventory.parallelStream().filter(apple -> apple.getWeight() > 150).collect(Collectors.toList());
    }
}

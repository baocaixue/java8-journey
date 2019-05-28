package com.isaac.java8.part1.chapter2;


/**
 * 谓词
 * 可以理解为：是符合（需求变动）条件的苹果么？
 *
 * 如果这里做了接口的实现，就成了策略模式了,策略模式的filterApple方法接收ApplePredicate对象
 *
 * 这就是行为参数化：让方法接受多种行为（或战略作为参数），并在内部使用，完成不同的行为。唯一的
 * 遗憾是如果用了传统的策略模式，参数还是需要传递一个实现了ApplePredicate的对象。Lambda解决了
 * 这种烦恼
 *
 * 演进历程：值参数化-->（策略模式）行为参数化-类-->匿名类-->Lambda
 */
@FunctionalInterface
public interface ApplePredicate {
    //boolean test(Apple apple);
    String print(Apple apple);
}

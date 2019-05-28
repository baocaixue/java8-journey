package com.isaac.java8.part1.chapter2;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import static java.util.Arrays.*;

@Data
@AllArgsConstructor
public class Apple {
    private String color;
    private Integer weight;

    private static void prettyPrintApple(List<Apple> inventory, ApplePredicate predicate){
        for (Apple apple : inventory) {
            String print = predicate.print(apple);
            System.out.println(print);
        }
    }

    public static void main(String[] args){
        List<Apple> inventory = asList(new Apple("blue", 12),new Apple("red",15));
        prettyPrintApple(inventory, Apple::getColor);
        prettyPrintApple(inventory, apple -> String.valueOf(apple.getWeight()));
    }
}

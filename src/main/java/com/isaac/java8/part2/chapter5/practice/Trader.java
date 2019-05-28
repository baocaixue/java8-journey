package com.isaac.java8.part2.chapter5.practice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 交易员
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode()
public class Trader {
//    @EqualsAndHashCode.Include
    private String name;
    private String city;
}

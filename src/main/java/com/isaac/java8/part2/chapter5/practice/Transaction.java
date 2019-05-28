package com.isaac.java8.part2.chapter5.practice;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 交易
 */
@Data
@AllArgsConstructor
public class Transaction {
    private Trader trader;
    private Integer year;
    private Integer value;
}

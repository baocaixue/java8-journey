package com.isaac.java8.part2.chapter5.practice;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class Solution {
    private final List<Transaction> transactions = new ArrayList<>();
    @Before
    public void setUp(){
        TransactionSupplier<Trader, Integer, Integer, Transaction> transactionSupplier = Transaction::new;
        BiFunction<String, String, Trader> traderSupplier = Trader::new;

        Trader bob = traderSupplier.apply("bob", "Beijing");
        Trader tom = traderSupplier.apply("tom", "Beijing");
        Trader isaac = traderSupplier.apply("isaac", "Shanghai");
        Trader jim = traderSupplier.apply("jim", "Hangzhou");
        Trader blue = traderSupplier.apply("blue", "Shanghai");
        Trader green = traderSupplier.apply("green", "Shanghai");

        transactions.add(transactionSupplier.get(bob, 2000, 10));
        transactions.add(transactionSupplier.get(bob, 2010, 15));
        transactions.add(transactionSupplier.get(bob, 2009, 66));
        transactions.add(transactionSupplier.get(tom, 2019, 41));
        transactions.add(transactionSupplier.get(tom, 2018, 35));
        transactions.add(transactionSupplier.get(isaac, 2017, 66));
        transactions.add(transactionSupplier.get(isaac, 2018, 78));
        transactions.add(transactionSupplier.get(isaac, 2019, 125));
        transactions.add(transactionSupplier.get(jim, 2011, 11));
        transactions.add(transactionSupplier.get(jim, 2014, 15));
        transactions.add(transactionSupplier.get(blue, 2019, 55));
        transactions.add(transactionSupplier.get(green, 2019, 45));
    }

    //找出2019年发生的所有交易，并按交易额排序（从低到高）
    @Test public void findAllTransactionAndSortValue() {
        List<Transaction> result = transactions.stream()
                .filter(transaction -> transaction.getYear() == 2019)
                .sorted(Comparator.comparing(Transaction::getValue))
                .collect(Collectors.toList());
        assertEquals(4, result.size());
        assertEquals(41, result.get(0).getValue().longValue());
        assertTrue(result.stream().allMatch(t -> t.getYear() == 2019));
    }

    //交易员都在哪些不同的城市工作过
    @Test public void findAllCity() {
        List<String> result = transactions.stream()
                .map(Transaction::getTrader)
                .map(Trader::getCity)
                .distinct()
                .collect(Collectors.toList());
        assertEquals(3, result.size());
    }

    //查询所有来自于上海的交易员，并按姓名排序
    @Test public void findAllSortedTraderFromShanghai() {
        List<Trader> result = transactions.stream()
                .map(Transaction::getTrader)
                .distinct()
                .filter(trader -> "Shanghai".equals(trader.getCity()))
                .sorted(Comparator.comparing(Trader::getName))
                .collect(Collectors.toList());
        assertEquals(3, result.size());
        assertEquals("blue", result.get(0).getName());
    }

    //返回所有交易员的姓名字符串，按字母排序降序
    @Test public void findAllSortedTraderNames(){
        List<String> result = transactions.stream()
                .map(Transaction::getTrader)
                .map(Trader::getName)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        assertEquals(6, result.size());
        assertEquals("tom", result.get(0));
    }

    //有没有交易员在杭州工作的
    @Test public void findAllTraderNoWorkInHangzhou() {
        Optional<Trader> result = transactions.stream()
                .map(Transaction::getTrader)
                .distinct()
                .filter(trader -> "Hangzhou".equals(trader.getCity()))
                .findAny();
        assertTrue(result.isPresent());
        assertEquals("jim", result.get().getName());
    }

    //打印生活在北京的交易员的所有交易额
    @Test public void calculateValueWhenTraderInBeijing() {
        Integer sum = transactions.stream()
                .filter(transaction -> transaction.getTrader().getCity().equals("Beijing"))
                .map(Transaction::getValue)
                .reduce(0, Integer::sum);
        assertEquals(167, sum.longValue());
    }

    //所有交易中，最高的交易额是多少
    @Test public void findMaxVale() {
        Integer max1 = transactions.stream()
                .max(Comparator.comparing(Transaction::getValue))
                .map(Transaction::getValue)
                .get();
        Integer max2 = transactions.stream()
                .map(Transaction::getValue)
                .reduce(0, Integer::max);
        assertEquals(125, max1.longValue());
        assertEquals(max1, max2);
    }

    //找到交易额最小的交易
    @Test public void finaMinValueTransaction() {
        Transaction result1 = transactions.stream()
                .min(Comparator.comparing(Transaction::getValue))
                .orElse(null);
        Transaction result2 = transactions.stream()
                .reduce((t1, t2) -> t1.getValue() < t2.getValue() ? t1 : t2)
                .orElse(null);
        assertNotNull(result1);
        assertNotNull(result2);

        assertEquals("bob", result1.getTrader().getName());
        assertEquals("bob", result2.getTrader().getName());
    }
}
interface TransactionSupplier<T1, T2, T3, R> {
    R get(T1 trader, T2 year, T3 value);
}

package com.isaac.java8.part1.chapter3;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.*;

import static org.junit.Assert.*;

public class Sample {

    /**
     * 方法引用的三种方式
     */
    @Test
    public void testFunctionReference() {
        String str = "123";
        /* 1.static function*/
        Integer result1 = apply(str, Integer::parseInt);
        assertTrue(result1 == 123);
        /* 2.instance function*/
        Integer result2 = apply(str, String::length);
        assertTrue(result2 == 3);
        /* 3.external instance function*/
        Sample s = new Sample();
        String result3 = apply(str, s::append);
        assertTrue(result3.endsWith("?"));

        /* sample */
        List<String> list = Arrays.asList("a", "b", "A", "B");
        list.sort(String::compareToIgnoreCase);
        assertEquals("A", list.get(1));
        BiPredicate<List<String>, String> contains = List::contains;
        boolean test = contains.test(list, "a");
        assertTrue(test);
    }

    private <T, R> R apply(T t, Function<T, R> f) {
        return f.apply(t);
    }

    private String append(String str) {
        return str + "?";
    }

    /**
     * 构造函数引用
     */
    @Test
    public void testConstructionReference() {
        Supplier<FrmUser> supplier = FrmUser::new;
        FrmUser user = supplier.get();
        assertNotNull(user);

        OwnerSupplier<String, Integer, Boolean, FrmUser> function = FrmUser::new;
        user = function.get("isaac", 24, true);
        assertNotNull(user);
        assertEquals("isaac", user.getName());
        assertTrue(user.getIsStudent());

    }

    /**
     * 复合Lambda表达式
     */
    @Test
    public void testMixLambda() {
        OwnerSupplier<String, Integer, Boolean, FrmUser> function = FrmUser::new;
        FrmUser isaac = function.get("isaac", 24, false);
        FrmUser tom = function.get("tom", 23, true);
        FrmUser jerry = function.get("jerry", 25, true);
        FrmUser green = function.get("green", 25, false);


        /* 对list中的user根据年龄逆序排序*/
        List<FrmUser> list = Arrays.asList(isaac, tom, jerry, green);
        list.sort(Comparator.comparing(FrmUser::getAge).reversed());
        assertEquals("jerry", list.get(0).getName());

        /* 比较器链，年龄相同按名字*/
        list.sort(Comparator.comparing(FrmUser::getAge).reversed().thenComparing(FrmUser::getName));
        assertEquals("green", list.get(0).getName());

        /* 谓词复合*/
        Predicate<FrmUser> isStudent = FrmUser::getIsStudent;
        Predicate<FrmUser> noStudent = isStudent.negate();
        Predicate<FrmUser> noStudentAndAge24 = noStudent.and(u -> u.getAge() == 24);
        boolean test = noStudentAndAge24.test(isaac);
        assertTrue(test);

        /* 复合函数*/
        Integer number = 0;
        Function<Integer, Integer> func1 = x -> x + 1;
        Function<Integer, Integer> func2 = x -> x * 5;

        Function<Integer, Integer> func = func1.andThen(func2);
        Integer result1 = func.apply(number);
        assertTrue(result1 == 5);

        //f(n),g(n),l(n) l(n)=f(g(n))<-->compose
        func = func1.compose(func2);
        Integer result2 = func.apply(number);
        assertTrue(result2 == 1);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class FrmUser {
        private String name;
        private Integer age;
        private Boolean isStudent;

    }
}

@FunctionalInterface
interface OwnerSupplier<T, O, U, R> {
    R get(T t, O o, U u);
}


package com.isaac.java8.part3.chapter8.pattern;

import lombok.AllArgsConstructor;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 策略设计模式：解决一类算法的通用解决方案，在运行时选择使用哪种方案
 * 包含三部分
 * -一个代表某个算法的接口（策略模式的接口）
 * -一个或多个该接口的具体实现
 * -一个或多个使用策略对象的客户
 *
 * 例子：验证输入的内容是否根据标准进行了恰当的格式化（比如只包含小写字母或数字）
 */
public class StrategyPatternSample {
    @Test public void testStrategy() {
        //不使用Lambda
        Validator implStrategyValidator1 = new Validator(new IsNumeric());
        Validator implStrategyValidator2 = new Validator(new IsAllLowerCase());
        assertFalse(implStrategyValidator1.validate("aaa"));
        assertTrue(implStrategyValidator2.validate("aaa"));

        //使用Lambda
        Validator lambdaValidator1 = new Validator(s -> s.matches("\\d+"));
        Validator lambdaValidator2 = new Validator(s -> s.matches("[a-z]+"));
        assertFalse(lambdaValidator1.validate("aaa"));
        assertTrue(lambdaValidator2.validate("aaa"));
    }
}

//策略接口
interface ValidationStrategy {
    boolean execute(String s);
}

//策略实现——这里是Lambda可以替代的
class IsAllLowerCase implements ValidationStrategy {

    @Override
    public boolean execute(String s) {
        return s.matches("[a-z]+");
    }
}
class IsNumeric implements ValidationStrategy {

    @Override
    public boolean execute(String s) {
        return s.matches("\\d+");
    }
}

//客户
@AllArgsConstructor
class Validator {
    private final ValidationStrategy strategy;

    public boolean validate (String s) {
        return strategy.execute(s);
    }
}

package com.isaac.java8.part3.chapter8.pattern;

import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * 责任链模式：一种创建处理对象序列（比如操作序列）的通用方案。一个处理对象可能需要完成一些工作后，将结果传递给另一个对象，这个对象接着做一些工作，再转交给下一个处理对象...
 * 通常，通过定义一个代表处理对象的抽象类实现，再抽象类中一个字段来记录后续对象
 */
public class ChainOfResponsibilityPatternSample {
    @Test public void testChainOfResponsibilityPattern() {
        ProcessingObject<String> p1 = new HeaderTextProcessing();
        ProcessingObject<String> p2 = new SpellCheckerProcessing();
        p1.setSuccessor(p2);
        String result = p1.handle("Aren't labdas really sexy?!!");
        Assert.assertEquals("From Isaac : Aren't lambdas really sexy?!!", result);

        //使用Lambda
        //这个模式看起来像在链接函数
        UnaryOperator<String> headerProcessing = (String text) -> "From Isaac : " + text;
        UnaryOperator<String> spellCheckerProcessing = (String text) -> text.replaceAll("labda", "lambda");
        Function<String, String> pipeline = headerProcessing.andThen(spellCheckerProcessing);
        String result1 = pipeline.apply("Aren't labdas really sexy?!!");
        Assert.assertEquals(result, result1);
    }
}

abstract class ProcessingObject<T> {
    @Setter
    protected ProcessingObject<T> successor;

    public T handle(T input) {
        T r = handleWork(input);
        if (successor != null) {
            return successor.handle(r);
        }
        return r;
    }

    abstract protected T handleWork(T input);
}

class HeaderTextProcessing extends ProcessingObject<String> {

    @Override
    protected String handleWork(String input) {
        return "From Isaac : " + input;
    }
}

class SpellCheckerProcessing extends ProcessingObject<String> {

    @Override
    protected String handleWork(String input) {
        return input.replaceAll("labda", "lambda");
    }
}

package com.isaac.java8.part3.chapter9;

import org.junit.Assert;
import org.junit.Test;

/**
 * 解决冲突规则
 * 1.类种的方法优先级最高。类或父类中声明的方法优先级高于任何声明为默认方法的优先级
 * 2.如果依据1无法判断，那么自接口的优先级更高：函数签名相同，优先选择拥有最具体实现的默认方法接口，即如果B继承了A，那么B就比A更具体
 * 3.如果还是无法判读，继承了多个接口的类必须通过显示覆盖和调用期望的方法，显式地选择使用哪种默认方法的实现
 */
public class ConflictSample {
    @Test public void test() {
        C c = new C();
        //规则2
        Assert.assertEquals("Hello from B", c.hello());
    }
}

interface A {
    default String hello() {
        return "Hello from A";
    }
}

interface B extends A{
    default String hello() {
        return "Hello from B";
    }
}

class C implements B,A {

}
/*
interface A {
    default void hello() {
        System.out.println("Hello from A");
    }
}

interface B {
    default void hello() {
        System.out.println("Hello from B");
    }
}

class C implements B,A {//编译不通过

}
 */

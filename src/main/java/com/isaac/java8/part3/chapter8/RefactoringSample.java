package com.isaac.java8.part3.chapter8;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Java8重构既有代码
 */
public class RefactoringSample {
    /**
     * 重构，将实现单一抽象方法的匿名类转换为Lambda表达式
     * 可以考虑从Lambda表达式到方法的引用的转换
     */
    @Test public void testAnonymousClassToLambda() {
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                System.out.println(this.getClass());
            }
        };
        Runnable r2 = () -> System.out.println(this.getClass());
        r1.run();
        r2.run();

        /*在某些情况下，将匿名类转换为Lambda表达式可能是一个比较复杂的过程。首先，匿名类和Lambda表达式中的this和super的含义是不同的。在匿
        名类中，this代表类自身，但是在Lambda中，代表的是包含类。其次，匿名类可以屏蔽包含类的变量，而Lambda表达式不能
        在涉及重载的上下文里，将匿名类转换为Lambda表达式可能导致最终的代码更加晦涩。实际上，匿名类的类型是在初始化时确定的，而Lambda表达式
        的类型取决于它的上下文。*/
        //doSomeThing(() -> System.out.println("hello"));//不知道是那个doSomeThing
        doSomeThing((Task) () -> System.out.println("hello"));
    }
    private void doSomeThing(Runnable r) {r.run();}
    private void doSomeThing(Task t) {t.execute();}

    /**
     * 从命令式的数据处理切换到Stream
     * 建议将所有使用迭代器这种数据处理模式处理集合的代码都转换成StreamAPI的方法
     */
    @Test public void testLambdaToReferenceMethod() {
        //命令式代码使用了两种模式：筛选和抽取，并混合在一起来
        List<Dish> menu = new ArrayList<>();
        List<String> dishNames = new ArrayList<>();
        for (Dish dish : menu) {
            if (dish.getCalories() > 300) {
                dishNames.add(dish.getName());
            }
        }
        //Stream API
        List<String> dishNames1 = menu.stream().filter(dish -> dish.getCalories() > 300).map(Dish::getName).collect(Collectors.toList());
    }

    @Data
    @AllArgsConstructor
    private static class Dish {
        private String name;
        private Boolean vegetarian;
        private Integer calories;
        private Type type;
    }

    private enum Type {MEAT, FISH, OTHER}

    private interface OwnerSupplier<T, T1, T2, T3, R> {
        R get(T name, T1 vegetarian, T2 calories, T3 type);
    }
}
interface Task {
    void execute();
}

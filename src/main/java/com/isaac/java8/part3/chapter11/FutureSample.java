package com.isaac.java8.part3.chapter11;

import org.junit.Test;

import java.util.concurrent.*;

/**
 * Future接口
 *
 * Future接口是在Java5中被引入的，设计初衷是对将来某个时刻会发生的结果建模。它建模了一种异步计算，返回一个执行结果的引用，当运算结束后，这个
 * 引用返回给调用方。在Future中触发那些潜在耗时的操作把调用线程解放出来，让它继续执行其他有价值的工作，而不需要呆呆地等待耗时的操作。
 */
public class FutureSample {
    @Test public void testFutureExec() {
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Double> future = executor.submit(this::doSomeLongComputation);
        //do something else
        System.out.println("else some thing");

        try {
            //避免长时间运行的操作永远不返回，推荐提供超时参数
            Double result = future.get(3, TimeUnit.SECONDS);
            System.out.println("result is " + result);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            if (e instanceof TimeoutException) {
                System.out.println("time out...");
            }else {
                e.printStackTrace();
            }
        }

    }

    private Double doSomeLongComputation() throws Exception{
        Thread.sleep(1000*2);
        System.out.println("do some long computation");
        return 1.0;
    }
}

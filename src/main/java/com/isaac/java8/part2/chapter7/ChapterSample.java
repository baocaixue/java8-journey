package com.isaac.java8.part2.chapter7;

import com.isaac.java8.part2.chapter7.fork_join_sample.ForkJoinSumCalculator;
import lombok.Data;
import org.junit.Test;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static java.util.stream.Collectors.*;

public class ChapterSample {
    /**
     * 并行流
     * 可以通过对收集源调用parallelStream方法来把集合转换为并行流。并行流就是一个把内容分成多个数据块，并用不同的线程分别处理每个数据块的流。
     */
    @Test public void testParallelStream() {
        //接受数字n作为参数，并返回从1到给定参数的所有数字的和。一个直接的方法就是生成一个无穷大的数字流，把它限制到给定数目，然后归约
        //对顺序流调用parallel方法并不意味着流本身有任何实际的变化。它在内部实际上就是设了一个boolean标志，表示你想让调用parallel之后进行
        //的所有操作都并行执行。类似的，对并行流调用sequential方法就可以让它变成顺序流

        //测量对前n个自然数求和的函数性能
        //System.out.println("Sequential sum1 done in : " + measureSumPerf(this::sequentialSum, 10000000) + "ms");
        //System.out.println("Sequential sum2 done in : " + measureSumPerf(this::iterativeSum, 10000000) + "ms");
        //函数并行
        //System.out.println("Parallel sum done in : " + measureSumPerf(this::parallelSum, 10000000) + "ms");
        long sequentialSumCost = measureSumPerf(this::iterativeSum, 10000000);
        long parallelSumCost = measureSumPerf(this::parallelSum, 10000000);
        assertTrue(sequentialSumCost < parallelSumCost);

        /*
         *造成并行版本比顺序版本慢很多的原因（iterate本质上是顺序的）：
         *  -iterate生成的是装箱的对象，必须拆箱才能求和
         *  -很难把iterate分成多个独立块来并行执行
         *  其实对顺序处理增加了开销，它还要每次求和操作分到一个不同的线程上
         */

        //使用更有针对性的方法——LongStream.rangedClosed没有装箱拆箱的开销；生成了数字范围，容易拆分独立的小块
        long correctParallelSumCost = measureSumPerf(this::rangedSum, 10000000);
        assertTrue(correctParallelSumCost < sequentialSumCost);
        //使用正确的数据结构然后使其并行工作能够保证最佳性能

        //并行化并不是没有代价的。很重要的一点是，要保证在内核中并行执行工作的时间比在内核之间传输数据的时间长。

        //正确使用并行流
        //错用并行流而产生错误的首要原因，就是使用的算法改变了某些共享状态。
        //该方法比性能糟糕更可怕，结果已经是不正确的了
        //measureSumPerf(this::sideEffectSum, 10000000);

        //ForkJoinSum
        //这里性能比并行流差，是因为必须要把数字流都放进一个long[],之后才能在ForkJoinSumCalculator任务中使用
        System.out.println("customize fork join sum cost : " + measureSumPerf(ForkJoinSumCalculator::forkJoinSum, 10000000) + "ms");
    }

    public long sequentialSum(long n) {
        return Stream.iterate(1L, i -> i + 1).limit(n).reduce(0L,Long::sum);
    }

    public long parallelSum(long n) {
        return Stream.iterate(1L, i -> i + 1).limit(n).parallel().reduce(0L, Long::sum);
    }
    /*没有装箱拆箱的开销；生成了数字范围，容易拆分独立的小块*/
    public long rangedSum(long n) {
        return LongStream.rangeClosed(1L, n).parallel().reduce(0L, Long::sum);
    }
    /*用传统的for循环的迭代版本执行会快很多，因为它更底层，更重要它不需要对原始类型做装箱或拆箱操作*/
    public long iterativeSum(long n) {
        long result = 0L;
        for (long i = 1; i <= n; i++) {
            result += i;
        }
        return result;
    }
    /*
     *测量对前n个自然数求和的函数性能
     */
    public long measureSumPerf(Function<Long, Long> adder, long n) {
        long fastest = Long.MAX_VALUE;
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            long sum = adder.apply(n);
            long duration = (System.nanoTime() - start) / 1000000;
            System.out.println("Result:" + sum);
            //返回最短的一次执行时间
            if (duration < fastest) fastest = duration;
        }
        return fastest;
    }

    /*共享累加器对前n个自然数求和*/
    public long sideEffectSum(long n) {
        //共享累加器
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n).parallel().forEach(accumulator::add);
        return accumulator.getTotal();
    }

    @Data
    private class Accumulator{
        private long total = 0L;
        public void add(long value) {total += value;}
    }
}

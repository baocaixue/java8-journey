package com.isaac.java8.part2.chapter5;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * 构建流
 */
public class BuildStreamSample {

    @Test public void testBuildStream() {
        /*由值创建流*/
        Stream<String> stringStream = Stream.of("Java8", "Lambdas", "In", "Action");
        stringStream.map(String::toUpperCase).forEach(System.out::println);

        /*由数组创建流*/
        int[] arr = {1, 2, 3, 4, 5};
        IntStream arrayStream = Arrays.stream(arr);
        int sum = arrayStream.sum();
        assert sum == 15;

        /*由文件生成流*/
        long uniqueWords = 0;
        try {
            Stream<String> lines = Files.lines(Paths.get("D:\\CodingLife\\IDE\\IdeaSpace\\java8\\src\\main\\java\\com\\isaac\\java8\\part2\\chapter5\\test.txt"), Charset.defaultCharset());
            uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(""))).distinct().count();
            System.out.println(uniqueWords);
        } catch (IOException e){
            e.printStackTrace();
        }

        /*由函数生成流 无限流*/
        //迭代
        Stream.iterate(0, n -> n +2)
                .limit(10)
                .forEach(System.out::println);
        //斐波拉契数列
        Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1], t[0] + t[1]})
                .limit(5)
                .map(t -> t[0])
                .forEach(System.out::println);
                //.forEach(t -> System.out.println("(" + t[0] + "," + t[1] + ")"));

        //生成，与iterate方法类似，generate方法也可以按需生成一个无限流。但generate不是依此对每个新生成的值应用函数的。它接受一个Supplier
        //类型的Lambda提供的新值
        Stream.generate(Math::random)
                .limit(5)
                .forEach(System.out::println);

    }
}

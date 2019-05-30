package com.isaac.java8.part2.chapter7;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 实现自己的Spliterator
 */
public class CustomizedSpliterator {
    /**
     * 一个迭代式字数统计方法
     */
    public int countWordsIteratively(String s) {
        long start = System.nanoTime();
        int counter = 0;
        boolean lastSpace = true;
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c)) {
                lastSpace = true;
            } else {
                if (lastSpace)
                    counter++;
                lastSpace = false;
            }
        }
        System.out.println("count words iteratively cost " + ((System.nanoTime()) - start) / 1000 + "ms");
        return counter;
    }

    /**
     * 函数式 流处理计数
     */
    public int countWordsByStream(Stream<Character> stream) {
        long start = System.nanoTime();
        WordCounter reduceWordCounter = stream.reduce(new WordCounter(0, true), WordCounter::accumulate, WordCounter::combine);
        Assert.assertNotNull(reduceWordCounter);
        System.out.println("count Words By Stream cost " + ((System.nanoTime()) - start) / 1000 + "ms");
        return reduceWordCounter.getCounter();
    }

    @Test
    public void testMethod() {
        final String SENTENCE = "Nel mezzo del cammin di nostra vita mi ritrovai in una selva oscura che la dritta via era smarrita";
        int count1 = this.countWordsIteratively(SENTENCE);
        Assert.assertEquals(19, count1);

        //函数式风格重写
        //非并行流
        Stream<Character> stream = IntStream.range(0, SENTENCE.length()).mapToObj(SENTENCE::charAt);
        int count2 = this.countWordsByStream(stream);
        Assert.assertEquals(19, count2);

        WordCounterSpliterator spliterator = new WordCounterSpliterator(SENTENCE);
        Stream<Character> stream1 = StreamSupport.stream(spliterator, true);
        int count3 = this.countWordsByStream(stream1);
        Assert.assertEquals(19, count3);
    }

    @Data
    @AllArgsConstructor
    private class WordCounter {
        private int counter;
        private boolean lastSpace;

        public WordCounter accumulate(Character c) {
            if (Character.isWhitespace(c)) {
                return lastSpace ? this : new WordCounter(counter, true);
            } else {
                return lastSpace ? new WordCounter(counter + 1, false) : this;
            }
        }

        public WordCounter combine(WordCounter wordCounter) {
            return new WordCounter(counter + wordCounter.counter, false);
        }
    }

    private class WordCounterSpliterator implements Spliterator<Character> {
        private final String string;
        private int currentChar = 0;

        public WordCounterSpliterator(String string) {
            this.string = string;
        }

        @Override
        public boolean tryAdvance(Consumer<? super Character> action) {
            //处理当前字符
            action.accept(string.charAt(currentChar++));
            return currentChar < string.length();
        }

        @Override
        public Spliterator<Character> trySplit() {
            int currentSize = string.length() - currentChar;
            if (currentSize < 10) {
                //粒度足够小，可以顺序处理
                return null;
            }
            for (int splitPos = currentSize / 2 + currentChar; splitPos < string.length(); splitPos++) {
                if (Character.isWhitespace(string.charAt(splitPos))) {
                    WordCounterSpliterator spliterator = new WordCounterSpliterator(string.substring(currentChar, splitPos));
                    currentChar = splitPos;
                    return spliterator;
                }
            }
            return null;
        }

        @Override
        public long estimateSize() {
            return string.length() - currentChar;
        }

        @Override
        public int characteristics() {
            return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
        }
    }
}

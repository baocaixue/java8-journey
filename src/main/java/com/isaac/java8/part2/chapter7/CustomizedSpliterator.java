package com.isaac.java8.part2.chapter7;

import org.junit.Test;

/**
 * 实现自己的Spliterator
 */
public class CustomizedSpliterator {
    /**
     * 一个迭代式字数统计方法
     */
    public int countWordsIteratively(String s) {
        int counter = 0;
        boolean lastSpace = true;
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c)) {
                lastSpace = true;
            } else  {
                if (lastSpace)
                    counter ++;
                lastSpace = false;
            }
        }
        return counter;
    }

    @Test
    public void testMethod() {
        final String SENTENCE = "Nel mezzo del cammin di nostra vita mi ritrovai in una selva oscura che la dritta via era smarrita";
        int count1 = this.countWordsIteratively(SENTENCE);
        System.out.println("Found " + count1 + " words");
    }
}

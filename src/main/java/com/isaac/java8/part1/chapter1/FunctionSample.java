package com.isaac.java8.part1.chapter1;

import java.io.File;
import java.io.FileFilter;

/**
 * Java8新增了函数——值的一种新形式
 * <p>
 * 编程语言的整个目的就在操作值，这些值因此被称作“一等值”（“一等公民”）
 * 其他很多Java概念（如方法和类等）则是“二等公民”
 * <p>
 * Java8让方法和Lambda成为“一等公民”
 */
public class FunctionSample {
    /* 方法引用 */

    //before java8
    public File[] findHiddenFiles(String filePath) {
        File[] hiddenFiles = new File(filePath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isHidden();
            }
        });
        return hiddenFiles;
    }

    //java8 方法引用
    public File[] findHiddenFilesUsingJava8(String filePath) {
        return new File(filePath).listFiles(File::isHidden);
    }

    /* Lambda——匿名函数 */
    //java8 Lambda
    public File[] findHiddenFilesUsingLambda(String filePath) {
        return new File(filePath).listFiles((file) -> file.isHidden());
    }
}

package com.isaac.java8.part3.chapter10;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * 有效地使用Optional类意味着需要对如何处理潜在缺失值进行全面的反思
 * 实际上，如果Optional类能够在这些API创建之初就存在的话，很多API的设计编写可能会大有不同。为了保持向后兼容，很难对老的Java API进行改动，让
 * 它们也使用Optional，但并不表示着我们什么也做不了。可以在自己的代码种添加一些工具方法，修复或绕过这些问题
 */
public class OptionalAdvance {

    @Test
    public void test() {
        //用Optional封装可能为null的值
        //现存Java API几乎都是通过返回一个null的方式表示需要值缺失，比如Map
        Map<String, String> map = new HashMap<>();
        Optional<String> optValue = Optional.ofNullable(map.get("key"));
        String value = optValue.orElse("Default Value");
        Assert.assertEquals("Default Value", value);

        //异常与Optional的对比
        Optional<Integer> optInt = OptionalAdvance.stringToInt("abc");
        Assert.assertEquals(0, optInt.orElse(0).longValue());

        Properties param = new Properties();
        param.setProperty("a", "5");
        param.setProperty("b", "true");
        param.setProperty("c", "-3");
        Assert.assertEquals(5, readDuration(param, "a"));
        Assert.assertEquals(0, readDuration(param, "b"));
        Assert.assertEquals(0, readDuration(param, "c"));
        Assert.assertEquals(0, readDuration(param, "d"));

    }

    /**
     * 由于某种原因，函数无法返回某个值，这时除了返回null，Java API比较常见的替代做法是抛出一个异常
     * OptionalInt基础类型的Optional不支持map等操作，不推荐使用
     */
    public static Optional<Integer> stringToInt(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * 从属性种读取值，该值是以秒为单位计量的一段时间，一段时间必须是正数
     */
    public int readDuration(Properties props, String name) {
//        Optional<String> optProperty = Optional.ofNullable(props.getProperty(name));
//        return OptionalAdvance.stringToInt(optProperty.orElse("")).filter(i -> i >= 0).orElse(0);
        return Optional.ofNullable(props.getProperty(name)).flatMap(OptionalAdvance::stringToInt).filter(i -> i >= 0).orElse(0);
    }

}

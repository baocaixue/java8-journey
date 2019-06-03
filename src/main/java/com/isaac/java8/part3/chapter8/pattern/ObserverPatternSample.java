package com.isaac.java8.part3.chapter8.pattern;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 观察者模式：默写事件发生时（如状态转换），如果一个对象（主题）需要自动通知其他对象（观察者）
 * <p>
 * 例子：为Twitter这样的应用涉及并实现一个定制化的通知系统：几家报纸机构都订阅了新闻，它们希望接收的新闻中包含它们感兴趣的关键字，能得到特别的通知
 */
public class ObserverPatternSample {
    @Test
    public void testObserverPattern() {
        //不使用Lambda
        Feed f = new Feed();
        f.registerObserver(new NYTimes());
        f.registerObserver(new Guardian());
        f.registerObserver(new LeMonde());
        f.notifyObservers("The queen said her favorite book is Java 8 in Action!");

        //Lambda
        //Observer接口的所有实现都提供了一个方法：notify，新闻到达，它们都只是对一段代码封装执行。Lambda表达式设计的初衷就是消除这样的僵化代码
        Feed f1 = new Feed();
        f1.registerObserver(tweet -> {
            if (tweet != null && tweet.contains("money")) System.out.println("Breaking news in NY! " + tweet);
        });
        f1.notifyObservers("money, NY can you see?");
    }
}

//观察者接口：将不同的观察者聚合在一起
interface Observer {
    void notify(String tweet);
}

class NYTimes implements Observer {

    @Override
    public void notify(String tweet) {
        if (tweet != null && tweet.contains("money")) {
            System.out.println("Breaking news in NY!" + tweet);
        }
    }
}

class Guardian implements Observer {

    @Override
    public void notify(String tweet) {
        if (tweet != null && tweet.contains("queen")) {
            System.out.println("Yet another news in London... " + tweet);
        }
    }
}

class LeMonde implements Observer {

    @Override
    public void notify(String tweet) {
        if (tweet != null && tweet.contains("wine")) {
            System.out.println("Today cheese, wine and news! " + tweet);
        }
    }
}

//主题
interface Subject {
    void registerObserver(Observer o);

    void notifyObservers(String tweet);
}

class Feed implements Subject {
    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void registerObserver(Observer o) {
        this.observers.add(o);
    }

    @Override
    public void notifyObservers(String tweet) {
        observers.forEach(o -> o.notify(tweet));
    }
}

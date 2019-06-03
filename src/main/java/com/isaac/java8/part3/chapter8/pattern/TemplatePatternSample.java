package com.isaac.java8.part3.chapter8.pattern;

import org.junit.Test;

import java.util.function.Consumer;

/**
 * 模板方法：采用某个算法框架，同时又希望有一定的灵活性
 * <p>
 * 例子：简单的在线银行应用。用户需要输入一个用户账户，之后应用才能从银行的数据库中获取信息，最终完成让客户满意的操作。
 * 不同分行让客户满意的方式可能不一样，比如给客户账户发放红利，或仅仅发送推广文件。
 */
public class TemplatePatternSample {
    @Test public void testTemplatePattern() {
        new OnlineBankingLambda().processCustomer(1001, c->{
            //分行让客户满意的方式
        });
    }
}

abstract class OnlineBanking {
    public void processCustomer(int id) {
        Customer c = DataBase.getCustomerWithId(id);
        makeCustomerHappy(c);
    }
    //子类提供差异化实现
    abstract void makeCustomerHappy(Customer c);
}
class Customer{}
class DataBase{public static Customer getCustomerWithId(int id){return new Customer();}}

class OnlineBankingLambda {

    //使用Lambda方式
    public void processCustomer(int id, Consumer<Customer> customerConsumer) {
        Customer c = DataBase.getCustomerWithId(id);
        customerConsumer.accept(c);
    }
}
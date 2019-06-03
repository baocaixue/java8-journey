package com.isaac.java8.part3.chapter8.pattern;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 工厂模式：无需向客户暴露实例化的逻辑就能完成对象的创建
 */
public class FactoryPatternSample {
}

class ProductFactoryLambda {
    private static final Map<String, Supplier<Product>> map = new HashMap<>();

    static {
        map.put("loan", Loan::new);
        map.put("stock", Stock::new);
        map.put("bond", Bond::new);
    }

    public static Product createProduct(String name) {
        Supplier<Product> p = map.get(name);
        if (p != null) return p.get();
        throw new RuntimeException("No such product " + name);
    }
}

class ProductFactory {
    public static Product createProduct(String name) {
        switch (name) {
            case "loan":
                return new Loan();
            case "stock":
                return new Stock();
            case "bond":
                return new Bond();
            default:
                throw new RuntimeException("No such product " + name);
        }
    }


}

interface Product {
}

class Loan implements Product {
}

class Stock implements Product {
}

class Bond implements Product {
}
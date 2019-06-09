package com.isaac.java8.part3.chapter11;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为了展示CompletableFuture的强大特性，我们会创建一个名为“最佳价格查询器”的应用，它会查询多个在线商店，根据给定的产品或服务找出最低的价格
 */
public class CompletableFutureSample {
    private static final Random random = new Random();
    private List<Shop> shops = Arrays.asList(
            new Shop("BestPrice"), new Shop("LetsSaveBig"), new Shop("MyFavoriteShop"),
            new Shop("BuyItAll"), new Shop("newShop"), new Shop("OtherShop"),
            new Shop("testShop"), new Shop("SuperShop"));

    private final Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), r -> {
        Thread t = new Thread();
        t.setDaemon(true);
        return t;
    });

    @Test
    public void test() {
        Shop shop = new Shop("BestShop");
        long start = System.nanoTime();
        Future<Double> futurePrice = shop.getPriceAsync1("A1");
        long invocationTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Invocation returned after " + invocationTime + " msecs");
        //可以在这时执行更多的任务，比如查询其他商店
        try {
            Double price = futurePrice.get();
            System.out.printf("Price is %.2f%n", price);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Price returned after " + retrievalTime + " mescs");
    }

    /**
     * 避免阻塞
     */
    @Test
    public void testAvoidBloking() {
        long start = System.nanoTime();

        //采用顺序
        List<String> resultList = findPrices(shops);
        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in " + duration + " msecs" + resultList);

        //并行流
        start = System.nanoTime();
        resultList = findPricesParallel(shops);
        duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in（Parallel） " + duration + " msecs" + resultList);

        //异步
        start = System.nanoTime();
        resultList = findPricesAsynchronized(shops);
        duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in（Asynchronized） " + duration + " msecs" + resultList);

        /*并行流版本与异步版本似乎性能相差无几，它们内部采用的同样通用的线程池，默认都是用固定数目的线程。然而，CompletableFuture具有一定的优势，执行器可配置*/
        /*N(thread) = N(cpu) * U(cpu) * (1 + W/C)*/
        /*N(thread):处理器核数目——Runtime.getRuntime().availableProcessors()得到；U(cpu)：希望的CPU利用率；W/C:等待时间与计算时间的比率*/
        final Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), r -> {
            Thread t = new Thread();
            t.setDaemon(true);
            return t;
        });
//        start = System.nanoTime();
//        resultList = findPricesAsynchronized1(shops, executor);
//        duration = (System.nanoTime() - start) / 1_000_000;
//        System.out.println("Done in（Asynchronized） " + duration + " msecs" + resultList);
    }

    /**
     * 对多个异步任务进行流水线操作
     * 假设所有的商店都同意使用一个集中式的折扣服务
     */
    @Test public void testAssemblyLineOperation() {
        long start = System.nanoTime();
        //List<String> resultList = findPrices("A1");
        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in " + duration + " msecs" );
    }

    /**
     *
     *  采用顺序查询所有商店的方式实现的findPrices方法
     */
    private List<String> findPrices(List<Shop> shops) {
        return shops.stream().map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice("A1"))).collect(Collectors.toList());
    }
    //并行流
    private List<String> findPricesParallel(List<Shop> shops) {
        return shops.parallelStream().map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice("A1"))).collect(Collectors.toList());
    }

    //异步
    private List<String> findPricesAsynchronized(List<Shop> shops) {
        List<CompletableFuture<String>> priceFutures = shops.stream().map(shop -> CompletableFuture.supplyAsync(() -> String.format("%s price is %.2f", shop.getName(), shop.getPrice("A1")))).collect(Collectors.toList());
        return priceFutures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    private List<String> findPricesAsynchronized1(List<Shop> shops, Executor executor) {
        List<CompletableFuture<String>> priceFutures = shops.stream().map(shop -> CompletableFuture.supplyAsync(() -> String.format("%s price is %.2f", shop.getName(), shop.getPrice("A1")),executor)).collect(Collectors.toList());
        return priceFutures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    //实现使用Discount服务的findPrices方法
    public List<String> findPrices(String product){
        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice1(product), executor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executor)))
                .collect(Collectors.toList());
        return priceFutures.stream().map(CompletableFuture::join).collect(Collectors.toList());

    }
    @Data
    @AllArgsConstructor
    private class Shop {
        private String name;

        public double getPrice(String product) {
            //查询商店的数据库或其他耗时的任务，示例用delay模拟
            return calculatePrice(product);
        }

        public String getPrice1(String product) {
            double price = calculatePrice(product);
            Discount.Code code = Discount.Code.values()[random.nextInt(Discount.Code.values().length)];
            return String.format("%s:%.2f:%s", name, price, code);
        }

        private double calculatePrice(String product) {
            delay();
            return random.nextDouble() * product.charAt(0) + product.charAt(1);
        }

        public Future<Double> getPriceAsync(String product) {
            CompletableFuture<Double> futurePrice = new CompletableFuture<>();
            new Thread(() -> {
                //可以在这里处理异常情况，防止future一直等待结果
                try {
                    double price = calculatePrice(product);
                    futurePrice.complete(price);
                } catch (Exception e) {
                    futurePrice.completeExceptionally(e);
                }
            }).start();
            return futurePrice;
        }

        /**
         * 使用工厂方法supplyAsync创建CompletableFuture对象
         */
        public Future<Double> getPriceAsync1(String product) {
            return CompletableFuture.supplyAsync(() -> calculatePrice(product));
        }
    }

    /**
     * 折扣
     */
    private static class Discount {
        public enum Code {
            NONE(0), SILVER(5), PLATINUM(15), DIAMOND(20);
            private final int percentage;
            Code(int percentage) {
                this.percentage = percentage;
            }
        }

        public static String applyDiscount(Quote quote) {
            return quote.getShopName() + " price is " + Discount.apply(quote.getPrice(), quote.getDiscountCode());
        }

        private static double apply(double price, Code code) {
            delay();
            return price * (100 - code.percentage) / 100;

        }

    }

    @Data@AllArgsConstructor
    private static class Quote {
        private String shopName;
        private double price;
        private Discount.Code discountCode;

        public static Quote parse(String s) {
            String[] split = s.split(":");
            String shopName = split[0];
            double price = Double.parseDouble(split[1]);
            Discount.Code discountCode = Discount.Code.valueOf(split[2]);
            return new Quote(shopName, price, discountCode);
        }
    }

    public static void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

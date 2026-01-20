package io.donkey.concurrent;

import java.util.concurrent.TimeUnit;

// Raw use of parameterized class 'java.util.concurrent.Future'
// public interface Future extends java.util.concurrent.Future<Object>
//public interface Future extends java.util.concurrent.Future {
// 或更准确地说：使用了 raw type，相当于擦除了泛型
// 将泛型向着 juc 包进行了传递
public interface Future<V> extends java.util.concurrent.Future<V> {

    boolean isSuccess();

    boolean isCancellable();

    Throwable cause();

    /**
     * 这里写一下泛型使用方法，我们 Future 扩展了 juc 的 Future 接口
     * - ? extends Future<? super V> 是 Java 中的 PECS 原则，Producer-Extends, Consumer-Super
     * - ? super V 表达某一个类型是 V 的 父类 或者 本身：如果 V = String ，那么 ? 可以是 Object、String、CharSequence
     * - ? extends Future<? super V> 外层表明：某个 future 类型 F 他是 Future<? super V> 的子类
     * Producer → Extends（往外“产出”数据，用 ? extends T）
     * Consumer → Super（往里“消费”数据，用 ? super T）
     * ------------------------------------------------------------------------------------------
     * Type Parameter & Wildcard 是 Java 泛型系统中2个不同的概念
     * - 泛型变量一般用在声明处
     * - 通配符一般在使用的时候。
     * Box<?> box;                     // ✔️ 合法
     * List<? extends Number> list;    // ✔️ 合法
     * class Box<?> { ... }            // ❌ 编译错误！不能用 ? 声明类
     * ------------------------------------------------------------------------------------------
     * 之所以接口定义的时候返回的都是接口本身，其实是一种链式调用。
     */
    Future<V> addListener(GenericFutureListener<? extends Future<? super V>> listener);

    Future<V> addListeners(GenericFutureListener<? extends Future<? super V>>... listeners);

    Future<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener);

    Future<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... listeners);

    Future<V> sync() throws InterruptedException;

    Future<V> syncUninterruptibly();

    Future<V> await() throws InterruptedException;

    Future<V> awaitUninterruptibly();

    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

    boolean await(long timeoutMillis) throws InterruptedException;

    boolean awaitUninterruptibly(long timeout, TimeUnit unit);

    boolean awaitUninterruptibly(long timeoutMillis);

    V getNow();

    @Override
    boolean cancel(boolean mayInterruptIfRunning);
}

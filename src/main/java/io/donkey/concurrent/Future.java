package io.donkey.concurrent;

// Raw use of parameterized class 'java.util.concurrent.Future'
// public interface Future extends java.util.concurrent.Future<Object>
//public interface Future extends java.util.concurrent.Future {
// 或更准确地说：使用了 raw type，相当于擦除了泛型
public interface Future<V> extends java.util.concurrent.Future<V> {
    boolean isSuccess();

    boolean isCancellable();

    Throwable cause();

    Future<V> addListener(GenericFutureListener<? extends Future<? super V>> listener);

    Future<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener);
}

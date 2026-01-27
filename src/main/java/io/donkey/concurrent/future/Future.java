package io.donkey.concurrent.future;

import io.donkey.concurrent.listener.GenericFutureListener;

import java.util.concurrent.TimeUnit;

public interface Future<V> extends java.util.concurrent.Future<V> {

    boolean isSuccess();

    boolean isCancellable();

    Throwable cause();

    Future<V> addListener(GenericFutureListener<? extends Future<? super V>> listener);

    Future<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener);

    Future<V> sync() throws InterruptedException;

    Future<V> await() throws InterruptedException;

    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

    V getNow();

    @Override
    boolean cancel(boolean mayInterruptIfRunning);
}

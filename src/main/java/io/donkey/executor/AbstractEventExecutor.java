package io.donkey.executor;

import io.donkey.concurrent.Promise.*;
import io.donkey.concurrent.future.FailedFuture;
import io.donkey.concurrent.future.Future;
import io.donkey.concurrent.future.ScheduledFuture;
import io.donkey.concurrent.future.SucceededFuture;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

/**
 * AbstractExecutorService 是 JDK 并发包（java.util.concurrent）中的核心抽象类，它的核心作用是：为 ExecutorService 接口提供高层方法的默认实现，
 * 子类只需实现底层的 execute(Runnable) 即可获得完整功能。这是典型的 "模板方法模式"（Template Method Pattern） 设计。
 */
public abstract class AbstractEventExecutor extends AbstractExecutorService implements EventExecutor {

    @Override
    public EventExecutor next() {
        return this;
    }

    // 是否在 EventLoop 中
    @Override
    public boolean inEventLoop() {
        return inEventLoop(Thread.currentThread());
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        return new EventExecutorIterator();
    }

    @Override
    public Future<?> shutdownGracefully() {
        return shutdownGracefully(2, 15, TimeUnit.SECONDS);
    }

    @Override
    @Deprecated
    public abstract void shutdown();

    @Override
    @Deprecated
    public List<Runnable> shutdownNow() {
        shutdown();
        return Collections.emptyList();
    }

    @Override
    public <V> Promise<V> newPromise() {
        return new DefaultPromise<V>(this);
    }

    public <V> ProgressivePromise<V> newProgressivePromise() {
        return new DefaultProgressivePromise<V>(this);
    }

    @Override
    public <V> Future<V> newSucceededFuture(V result) {
        return new SucceededFuture<V>(this, result);
    }

    @Override
    public <V> Future<V> newFailedFuture(Throwable cause) {
        return new FailedFuture<V>(this, cause);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return (Future<?>) super.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return (Future<T>) super.submit(task, result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return (Future<T>) super.submit(task);
    }

    @Override
    protected final <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new PromiseTask<T>(this, runnable, value);
    }

    @Override
    protected final <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new PromiseTask<T>(this, callable);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay,
                                       TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    /**
     * 这玩意儿只能迭代一个。。。
     */
    private final class EventExecutorIterator implements Iterator<EventExecutor> {
        private boolean nextCalled; // default value = false

        @Override
        public boolean hasNext() {
            return !nextCalled;
        }

        @Override
        public EventExecutor next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            nextCalled = true;
            return AbstractEventExecutor.this;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("read-only");
        }
    }
}

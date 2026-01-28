package io.donkey.concurrent.promise;

import io.donkey.concurrent.eventExecutor.EventExecutor;
import io.donkey.concurrent.future.Future;
import io.donkey.concurrent.listener.GenericFutureListener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DefaultPromise<V> implements Promise<V> {

    private Throwable cause; // 异常

    private final EventExecutor executor; // 线程

    private volatile Object result; // 结果、状态

    public DefaultPromise(EventExecutor executor) {
        if (executor == null) {
            throw new NullPointerException("executor");
        }
        this.executor = executor;
    }

    protected EventExecutor executor() {
        return executor;
    }


    @Override
    public Promise<V> setSuccess(V result) {
        if (isDone()) {
            return this;
        }

        synchronized (this) {
            // Allow only once.
            if (isDone()) {
                return this;
            }
            if (result == null) {
                this.result = State.SUCCESS;
            } else {
                this.result = result;
            }
            notifyAll();
        }
        return this;
    }

    @Override
    public boolean trySuccess(V result) {
        return false;
    }

    @Override
    public Promise<V> setFailure(Throwable cause) {
        if (cause == null) {
            throw new NullPointerException("cause");
        }
        if (isDone())
            return null;

        synchronized (this) {
            if (isDone())
                return null;
            this.cause = cause;
            this.result = cause;
            return this;
        }
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        return false;
    }

    @Override
    public boolean setUncancellable() {
        return false;
    }

    @Override
    public boolean isSuccess() {
        Object result = this.result;
        if (result == null || result == State.UNCANCELLABLE) {
            return false;
        }
        return cause == null;
    }

    @Override
    public boolean isCancellable() {
        return result == null;
    }

    @Override
    public Throwable cause() {
        return null;
    }

    @Override
    public Promise<V> addListener(GenericFutureListener<? extends Future<? super V>> listener) {
        return null;
    }

    @Override
    public Promise<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener) {
        return null;
    }

    @Override
    public Promise<V> await() throws InterruptedException {
        return null;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public V getNow() {
        return null;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return result != null && result != State.UNCANCELLABLE && result != State.CAUSE && cause == null;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    @Override
    public Promise<V> sync() throws InterruptedException {
        return null;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
    }

    private enum State {
        SUCCESS,
        UNCANCELLABLE,
        CAUSE
    }
}

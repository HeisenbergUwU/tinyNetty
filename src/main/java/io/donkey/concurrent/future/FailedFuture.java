package io.donkey.concurrent.future;

import io.donkey.executor.EventExecutor;

public final class FailedFuture<V> extends CompleteFuture<V> {

    private final Throwable cause;

    /**
     * Creates a new instance.
     *
     * @param executor the {@link EventExecutor} associated with this future
     * @param cause    the cause of failure
     */
    public FailedFuture(EventExecutor executor, Throwable cause) {
        super(executor);
        if (cause == null) {
            throw new NullPointerException("cause");
        }
        this.cause = cause;
    }

    @Override
    public Throwable cause() {
        return cause;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public Future<V> sync() {
        throw (RuntimeException) cause;
    }

    @Override
    public Future<V> syncUninterruptibly() {
        throw (RuntimeException) cause;
    }

    @Override
    public V getNow() {
        return null;
    }
}

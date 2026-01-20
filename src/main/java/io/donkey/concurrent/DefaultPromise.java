package io.donkey.concurrent;

import io.donkey.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DefaultPromise<V> extends AbstractFuture<V> implements Promise<V> {
    private static final int MAX_LISTENER_STACK_DEPTH = 8;
    private static final Signal SUCCESS = Signal.valueOf(DefaultPromise.class.getName() + ".SUCCESS");
    private static final Signal UNCANCELLABLE = Signal.valueOf(DefaultPromise.class.getName() + ".UNCANCELLABLE");
    private static final CauseHolder CANCELLATION_CAUSE_HOLDER = new CauseHolder(new CancellationException());

    static {
        CANCELLATION_CAUSE_HOLDER.cause.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
    }

    private final EventExecutor executor;

    private volatile Object result;

    private LateListeners lateListeners;

    private Object listeners;
    private short waiters;

    public DefaultPromise(EventExecutor executor) {
        if (executor == null) {
            throw new NullPointerException("executor");
        }
        this.executor = executor;
    }

    @Override
    public Promise<V> setSuccess(V result) {
        return null;
    }

    @Override
    public boolean trySuccess(V result) {
        return false;
    }

    @Override
    public Promise<V> setFailure(Throwable cause) {
        return null;
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
        return false;
    }

    @Override
    public boolean isCancellable() {
        return false;
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
    public Promise<V> addListeners(GenericFutureListener<? extends Future<? super V>>... listeners) {
        return null;
    }

    @Override
    public Promise<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener) {
        return null;
    }

    @Override
    public Promise<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... listeners) {
        return null;
    }

    @Override
    public Promise<V> await() throws InterruptedException {
        return null;
    }

    @Override
    public Promise<V> awaitUninterruptibly() {
        return null;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public boolean await(long timeoutMillis) throws InterruptedException {
        return false;
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        return false;
    }

    @Override
    public boolean awaitUninterruptibly(long timeoutMillis) {
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
        return false;
    }

    @Override
    public Promise<V> sync() throws InterruptedException {
        return null;
    }

    @Override
    public Promise<V> syncUninterruptibly() {
        return null;
    }

    protected EventExecutor executor() {
        return executor;
    }

    private final class LateListeners extends ArrayDeque<GenericFutureListener<?>> implements Runnable {
        private static final long serialVersionUID = -687137418080392244L;

        LateListeners() {
            super(2);
        }

        @Override
        public void run() {
            final EventExecutor executor = executor();

            if (listeners == null || executor ==) {

            }
        }
    }


    protected StringBuilder toStringBuilder() {
        StringBuilder buf = new StringBuilder(64)
                .append(StringUtil.simpleClassName(this))
                .append('@')
                .append(Integer.toHexString(hashCode()));

        Object result = this.result;
        if (result == SUCCESS) {
            buf.append("(success)");
        } else if (result == UNCANCELLABLE) {
            buf.append("(uncancellable)");
        } else if (result instanceof CauseHolder) {
            buf.append("(failure: ")
                    .append(((CauseHolder) result).cause)
                    .append(')');
        } else if (result != null) {
            buf.append("(success: ")
                    .append(result)
                    .append(')');
        } else {
            buf.append("(incomplete)");
        }

        return buf;
    }

    private static final class CauseHolder {
        final Throwable cause;

        CauseHolder(Throwable cause) {
            this.cause = cause;
        }
    }

    private final class LateListenerNotifier implements Runnable {
        private GenericFutureListener<?> l;

        LateListenerNotifier(GenericFutureListener<?> l) {
            this.l = l;
        }

        @Override
        public void run() {
            LateListeners lateListeners = DefaultPromise.this.lateListeners;
            if (l != null) {
                if (lateListeners == null) {
                    DefaultPromise.this.lateListeners = lateListeners = new LateListeners();
                }
                lateListeners.add(l);
                l = null;
            }
            lateListeners.run();
        }
    }
}
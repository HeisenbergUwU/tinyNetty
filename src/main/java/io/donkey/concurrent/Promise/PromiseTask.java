package io.donkey.concurrent.Promise;

import io.donkey.executor.EventExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

// 如果使用 Callable 的话，必须使用FutureTask 包装 Callable。
//                try {
//                    result = c.call();
//                    ran = true;
//                } catch (Throwable ex) {
//                    result = null;
//                    ran = false;
//                    setException(ex);
//                }
//                if (ran)
//                    set(result);
//            }
// DefaultPromise 本质上就是一个异步操作的结果容器 + 状态机 + 通知中心
public class PromiseTask<V> extends DefaultPromise<V> implements RunnableFuture<V> {

    protected static <T> Callable<T> toCallable(Runnable runnable, T result) {
        return new RunnableAdapter<T>(runnable, result);
    }

    protected final Callable<V> task;

    public PromiseTask(EventExecutor executor, Runnable runnable, V result) {
        this(executor, toCallable(runnable, result));
    }

    public PromiseTask(EventExecutor executor, Callable<V> callable) {
        super(executor);
        task = callable;
    }

    private record RunnableAdapter<T>(Runnable task, T result) implements Callable<T> {

        @Override
            public T call() throws Exception {
                task.run();
                return result;
            }

            @Override
            public String toString() {
                return "Callable(task: " + task + ", result: " + result + ')';
            }
        }

    @Override
    public void run() {
        try {
            if (setUncancellableInternal()) {
                V result = task.call();
                setSuccessInternal(result);
            }
        } catch (Throwable e) {
            setFailureInternal(e);
        }
    }

    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public final int hashCode() {
        // 基于地址计算的 hashcode，是懒计算的。
        return System.identityHashCode(this);
    }


    @Override
    public final Promise<V> setFailure(Throwable cause) {
        throw new IllegalStateException();
    }

    protected final Promise<V> setFailureInternal(Throwable cause) {
        super.setFailure(cause);
        return this;
    }

    @Override
    public final boolean tryFailure(Throwable cause) {
        return false;
    }

    protected final boolean tryFailureInternal(Throwable cause) {
        return super.tryFailure(cause);
    }

    @Override
    public final Promise<V> setSuccess(V result) {
        throw new IllegalStateException();
    }

    protected final Promise<V> setSuccessInternal(V result) {
        super.setSuccess(result);
        return this;
    }

    @Override
    public final boolean trySuccess(V result) {
        return false;
    }

    protected final boolean trySuccessInternal(V result) {
        return super.trySuccess(result);
    }

    @Override
    public final boolean setUncancellable() {
        throw new IllegalStateException();
    }

    protected final boolean setUncancellableInternal() {
        return super.setUncancellable();
    }

    @Override
    protected StringBuilder toStringBuilder() {
        StringBuilder buf = super.toStringBuilder();
        buf.setCharAt(buf.length() - 1, ',');

        return buf.append(" task: ")
                .append(task)
                .append(')');
    }

}

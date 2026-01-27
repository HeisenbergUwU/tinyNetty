package io.donkey.executor;

import io.donkey.concurrent.future.Future;
import io.donkey.concurrent.Promise.Promise;
// EventExecutor 是 执行 task 的
public interface EventExecutor extends EventExecutorGroup {

    @Override
    EventExecutor next();

    EventExecutorGroup parent();

    boolean inEventLoop();

    boolean inEventLoop(Thread thread);

    <V> Promise<V> newPromise();

    <V> Future<V> newSucceededFuture(V result);

    <V> Future<V> newFailedFuture(Throwable cause);
}

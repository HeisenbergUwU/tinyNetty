package io.donkey.executor;
// EventLoop 是用来执行 IO 操作的专用的
public interface EventLoop extends EventExecutor, EventLoopGroup {
    @Override
    EventLoopGroup parent();
}

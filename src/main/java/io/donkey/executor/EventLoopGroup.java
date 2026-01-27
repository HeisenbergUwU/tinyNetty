package io.donkey.executor;

import io.donkey.channel.Channel;
import io.donkey.channel.ChannelFuture;
import io.donkey.channel.ChannelPromise;

public interface EventLoopGroup extends EventExecutorGroup {
    /**
     * Return the next {@link EventLoop} to use
     */
    @Override
    EventLoop next();

    /**
     * Register a {@link Channel} with this {@link EventLoop}. The returned {@link ChannelFuture}
     * will get notified once the registration was complete.
     */
    ChannelFuture register(Channel channel);

    /**
     * Register a {@link Channel} with this {@link EventLoop}. The passed {@link ChannelFuture}
     * will get notified once the registration was complete and also will get returned.
     */
    ChannelFuture register(Channel channel, ChannelPromise promise);
}

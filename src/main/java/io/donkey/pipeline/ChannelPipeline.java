package io.donkey.pipeline;

import io.donkey.channel.handler.ChannelHandler;
import io.donkey.channel.handler.ChannelHandlerContext;

import java.util.Map;

public interface ChannelPipeline
        extends Iterable<Map.Entry<String, ChannelHandler>> {
    ChannelPipeline addFirst(String name, ChannelHandler handler);

    ChannelHandler remove(String name);

    ChannelHandler first();

    ChannelHandler last();

    ChannelHandler get(String name);

    ChannelHandlerContext context(String name);
}

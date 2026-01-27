package io.donkey.pipeline;

import io.donkey.channel.handler.ChannelHandler;
import io.donkey.channel.handler.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

@Slf4j
final class DefaultChannelPipeline implements ChannelPipeline {
    private static final WeakHashMap<Class<?>, String>[] nameCaches =
            new WeakHashMap[Runtime.getRuntime().availableProcessors()];

    static {
        for (int i = 0; i < nameCaches.length; i++) {
            nameCaches[i] = new WeakHashMap<Class<?>, String>();
        }
    }

    @Override
    public ChannelPipeline addFirst(String name, ChannelHandler handler) {
        return null;
    }

    @Override
    public ChannelHandler remove(String name) {
        return null;
    }

    @Override
    public ChannelHandler first() {
        return null;
    }

    @Override
    public ChannelHandler last() {
        return null;
    }

    @Override
    public ChannelHandler get(String name) {
        return null;
    }

    @Override
    public ChannelHandlerContext context(String name) {
        return null;
    }

    @Override
    public Iterator<Map.Entry<String, ChannelHandler>> iterator() {
        return null;
    }
}

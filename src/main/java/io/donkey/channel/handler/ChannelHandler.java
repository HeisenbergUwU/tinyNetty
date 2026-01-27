package io.donkey.channel.handler;

import java.lang.annotation.*;

public interface ChannelHandler {

    /**
     * Gets called after the {@link ChannelHandler} was added to the actual context and it's ready to handle events.
     */
    void handlerAdded(ChannelHandlerContext ctx) throws Exception;

    /**
     * Gets called after the {@link ChannelHandler} was removed from the actual context and it doesn't handle events
     * anymore.
     */
    void handlerRemoved(ChannelHandlerContext ctx) throws Exception;

    /**
     * Gets called if a {@link Throwable} was thrown.
     */
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception;
    
    @Inherited
    @Documented
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Sharable {
        // no value
    }
}

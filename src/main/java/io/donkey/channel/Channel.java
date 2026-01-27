package io.donkey.channel;

import io.donkey.buffer.ByteBufAllocator;
import io.donkey.common.AttributeMap;
import io.donkey.executor.EventLoop;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public interface Channel extends AttributeMap, Comparable<Channel> {

    EventLoop eventLoop();

    /**
     * Returns the parent of this channel.
     *
     * @return the parent channel.
     * {@code null} if this channel does not have a parent channel.
     */
    Channel parent();

    /**
     * Returns {@code true} if the {@link Channel} is open and may get active later
     */
    boolean isOpen();

    /**
     * Returns {@code true} if the {@link Channel} is registered with an {@link EventLoop}.
     */
    boolean isRegistered();

    /**
     * Return {@code true} if the {@link Channel} is active and so connected.
     */
    boolean isActive();

    SocketAddress localAddress();

    SocketAddress remoteAddress();


    ChannelFuture closeFuture();

    /**
     * Returns {@code true} if and only if the I/O thread will perform the
     * requested write operation immediately.  Any write requests made when
     * this method returns {@code false} are queued until the I/O thread is
     * ready to process the queued write requests.
     */
    boolean isWritable();

    /**
     * Returns an <em>internal-use-only</em> object that provides unsafe operations.
     */
    Unsafe unsafe();

    /**
     * Return the assigned {@link ChannelPipeline}.
     */
    ChannelPipeline pipeline();

    /**
     * Return the assigned {@link ByteBufAllocator} which will be used to allocate {@link ByteBuf}s.
     */
    ByteBufAllocator alloc();

    /**
     * Return a new {@link ChannelPromise}.
     */
    ChannelPromise newPromise();

    ChannelFuture bind(SocketAddress localAddress);

    ChannelFuture connect(SocketAddress remoteAddress);

    ChannelFuture disconnect();

    ChannelFuture close();

    Channel read();

    ChannelFuture write(Object msg);

    /**
     * Request to write a message via this {@link Channel} through the {@link ChannelPipeline}.
     * This method will not request to actual flush, so be sure to call {@link #flush()}
     * once you want to request to flush all pending data to the actual transport.
     */
    ChannelFuture write(Object msg, ChannelPromise promise);

    /**
     * Request to flush all pending messages.
     */
    Channel flush();

    /**
     * Shortcut for call {@link #write(Object, ChannelPromise)} and {@link #flush()}.
     */
    ChannelFuture writeAndFlush(Object msg, ChannelPromise promise);

    /**
     * Shortcut for call {@link #write(Object)} and {@link #flush()}.
     */
    ChannelFuture writeAndFlush(Object msg);

    /**
     * <em>Unsafe</em> operations that should <em>never</em> be called from user-code. These methods
     * are only provided to implement the actual transport, and must be invoked from an I/O thread except for the
     * following methods:
     * <ul>
     *   <li>{@link #localAddress()}</li>
     *   <li>{@link #remoteAddress()}</li>
     *   <li>{@link #closeForcibly()}</li>
     *   <li>{@link #register(EventLoop, ChannelPromise)}</li>
     *   <li>{@link #voidPromise()}</li>
     * </ul>
     */
    interface Unsafe {
        /**
         * Return the {@link SocketAddress} to which is bound local or
         * {@code null} if none.
         */
        SocketAddress localAddress();

        /**
         * Return the {@link SocketAddress} to which is bound remote or
         * {@code null} if none is bound yet.
         */
        SocketAddress remoteAddress();

        /**
         * Register the {@link Channel} of the {@link ChannelPromise} with the {@link EventLoop} and notify
         * the {@link ChannelFuture} once the registration was complete.
         */
        void register(EventLoop eventLoop, ChannelPromise promise);

        /**
         * Bind the {@link SocketAddress} to the {@link Channel} of the {@link ChannelPromise} and notify
         * it once its done.
         */
        void bind(SocketAddress localAddress, ChannelPromise promise);

        /**
         * Connect the {@link Channel} of the given {@link ChannelFuture} with the given remote {@link SocketAddress}.
         * If a specific local {@link SocketAddress} should be used it need to be given as argument. Otherwise just
         * pass {@code null} to it.
         * <p>
         * The {@link ChannelPromise} will get notified once the connect operation was complete.
         */
        void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise);

        /**
         * Disconnect the {@link Channel} of the {@link ChannelFuture} and notify the {@link ChannelPromise} once the
         * operation was complete.
         */
        void disconnect(ChannelPromise promise);

        /**
         * Close the {@link Channel} of the {@link ChannelPromise} and notify the {@link ChannelPromise} once the
         * operation was complete.
         */
        void close(ChannelPromise promise);

        /**
         * Closes the {@link Channel} immediately without firing any events.  Probably only useful
         * when registration attempt failed.
         */
        void closeForcibly();

        /**
         * Deregister the {@link Channel} of the {@link ChannelPromise} from {@link EventLoop} and notify the
         * {@link ChannelPromise} once the operation was complete.
         */
        void deregister(ChannelPromise promise);

        /**
         * Schedules a read operation that fills the inbound buffer of the first {@link ChannelInboundHandler} in the
         * {@link ChannelPipeline}.  If there's already a pending read operation, this method does nothing.
         */
        void beginRead();

        /**
         * Schedules a write operation.
         */
        void write(Object msg, ChannelPromise promise);

        /**
         * Flush out all write operations scheduled via {@link #write(Object, ChannelPromise)}.
         */
        void flush();

        /**
         * Return a special ChannelPromise which can be reused and passed to the operations in {@link Unsafe}.
         * It will never be notified of a success or error and so is only a placeholder for operations
         * that take a {@link ChannelPromise} as argument but for which you not want to get notified.
         */
        ChannelPromise voidPromise();

    }

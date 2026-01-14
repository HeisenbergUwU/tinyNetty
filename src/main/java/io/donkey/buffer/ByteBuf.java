package io.donkey.buffer;

import io.donkey.common.ReferenceCounted;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * 大端序：从左到右写 → 1 2 3 4（高位在前）
 * 小端序：从右到左写 → 4 3 2 1（低位在前）
 */
public abstract class ByteBuf implements ReferenceCounted, Comparable<ByteBuf> {

    public abstract int capacity();

    public abstract ByteBuf capacity(int newCapacity);

    public abstract int maxCapacity();

    public abstract ByteBufAllocator alloc();

    // 下一个要读的位置
    public abstract int readerIndex();

    // 设定下一个要读的位置
    public abstract ByteBuf readerIndex(int readerIndex);

    // 下一个要写的位置
    public abstract int writerIndex();

    // 设定下一个要写的位置
    public abstract ByteBuf writerIndex(int writerIndex);

    // 设定 读写的位置
    public abstract ByteBuf setIndex(int readerIndex, int writerIndex);

    public abstract boolean isWritable(int numBytes);

    public abstract int readableBytes();

    public abstract int writableBytes();

    public abstract int maxWritableBytes();

    /**
     * this.writerIndex - this.readerIndex greater than 0 。是否可读
     */
    public abstract boolean isReadable();

    public abstract boolean isReadable(int size);

    /**
     * Returns {@code true}
     * if and only if {@code (this.capacity - this.writerIndex)} is greater
     * than {@code 0}.
     */
    public abstract boolean isWritable();


    public abstract ByteBuf clear();

    // 标记 readerIndex，可以重新回到 marked 位置
    public abstract ByteBuf markReaderIndex();

    public abstract ByteBuf resetReaderIndex();

    public abstract ByteBuf markWriterIndex();

    public abstract ByteBuf resetWriterIndex();

    /**
     * 用于回收已读内存空间，用于释放缓缓冲区前面已经消费的空间。
     */
    public abstract ByteBuf ensureWritable(int minWritableBytes);

    public abstract ByteBuffer[] nioBuffers();

    public abstract int ensureWritable(int minWritableBytes, boolean force);

    public abstract ByteBuf setZero(int index, int length);

    public abstract ByteBuf writeZero(int length);

    public abstract int indexOf(int fromIndex, int toIndex, byte value);

    public abstract ByteBuf copy();

    public abstract int nioBufferCount();

    public abstract ByteBuffer nioBuffer();

    public abstract boolean hasArray();

    public abstract byte[] array();

    public abstract ByteBuffer internalNioBuffer(int index, int length);

    public abstract ByteBuffer nioBuffer(int index, int length);

    public abstract ByteBuffer[] nioBuffers(int index, int length);

    public abstract int arrayOffset();

    public abstract boolean hasMemoryAddress();

    public abstract long memoryAddress();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int compareTo(ByteBuf buffer);

    @Override
    public abstract String toString();

    @Override
    public abstract ByteBuf retain(int increment);

    @Override
    public abstract ByteBuf retain();

    public abstract ByteBuf copy(int index, int length);

    // --- 以下是一些读取数据的方法，很多可以通过重载实现
    public abstract byte getByte(int index);

    public abstract byte readByte();

    public abstract ByteBuf setByte(int index, int value);

    public abstract ByteBuf writeByte(int value);

    public abstract String toString(Charset charset);

    public abstract String toString(int index, int length, Charset charset);
}

package io.donkey.buffer;

import io.donkey.common.ReferenceCounted;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
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

    public abstract ByteBuf setByte(int index, int value);

    public abstract ByteBuf clear();

    // 标记 readerIndex，可以重新回到 marked 位置
    public abstract ByteBuf markReaderIndex();

    public abstract ByteBuf resetReaderIndex();

    public abstract ByteBuf markWriterIndex();

    public abstract ByteBuf resetWriterIndex();

    /**
     * 用于回收已读内存空间，用于释放缓缓冲区前面已经消费的空间。
     */
    public abstract ByteBuf discardReadBytes();

    public abstract ByteBuf ensureWritable(int minWritableBytes);

    public abstract int ensureWritable(int minWritableBytes, boolean force);

    // --- 以下是一些读取数据的方法，很多可以通过重载实现
    public abstract byte getByte(int index);

    public abstract ByteBuf getBytes(int index, byte[] dst);

    public abstract ByteBuf getBytes(int index, ByteBuffer dst);

    // setBytes  5 个
    public abstract ByteBuf setBytes(int index, ByteBuf src);

    public abstract ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length);

    public abstract ByteBuf setBytes(int index, byte[] src, int srcIndex, int length);

    public abstract ByteBuf setBytes(int index, byte[] src);

    public abstract ByteBuf setBytes(int index, ByteBuffer src);

    public abstract ByteBuf setZero(int index, int length);

    public abstract ByteBuf readBytes(int length);

    public abstract ByteBuf readBytes(byte[] dst);

    public abstract ByteBuf readBytes(ByteBuffer dst);

    public abstract ByteBuf skipBytes(int length);

    public abstract ByteBuf writeBytes(ByteBuf src);

    public abstract ByteBuf writeBytes(byte[] src);

    public abstract ByteBuf writeBytes(ByteBuffer src);

    public abstract ByteBuf writeZero(int length);

    public abstract int indexOf(int fromIndex, int toIndex, byte value);

    public abstract int bytesBefore(byte value);

    public abstract int bytesBefore(int length, byte value);

    public abstract int bytesBefore(int index, int length, byte value);

    public abstract ByteBuf copy();

    public abstract ByteBuf slice();

    public abstract ByteBuf duplicate();

    public abstract int nioBufferCount();

    public abstract ByteBuffer nioBuffer();

    public abstract boolean hasArray();

    public abstract byte[] array();

    public abstract int arrayOffset();

    public abstract boolean hasMemoryAddress();

    public abstract long memoryAddress();

    public abstract String toString(Charset charset);

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

    public abstract ByteBuf setBytes(int index, ByteBuf src, int length);

    // readByte
    public abstract byte readByte();
}

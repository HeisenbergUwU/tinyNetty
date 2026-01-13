package io.donkey.buffer;


import io.donkey.util.internal.EmptyArrays;
import io.donkey.util.internal.StringUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.charset.Charset;

/**
 * 空 ByteBuf
 */
public final class EmptyByteBuf extends ByteBuf {

    private static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.allocate(0);
    private static final long EMPTY_BYTE_BUFFER_ADDRESS;

    static {
        EMPTY_BYTE_BUFFER_ADDRESS = 0;
    }

    private final ByteBufAllocator alloc;
    private final String str;

    public EmptyByteBuf(ByteBufAllocator alloc) {
        this(alloc, ByteOrder.BIG_ENDIAN);
    }

    private EmptyByteBuf(ByteBufAllocator alloc, ByteOrder order) {
        if (alloc == null) {
            throw new NullPointerException("alloc");
        }

        this.alloc = alloc;
        str = StringUtil.simpleClassName(this) + (order == ByteOrder.BIG_ENDIAN ? "BE" : "LE");
    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBufAllocator alloc() {
        return alloc;
    }

    @Override
    public int maxCapacity() {
        return 0;
    }

    @Override
    public int readerIndex() {
        return 0;
    }

    @Override
    public ByteBuf readerIndex(int readerIndex) {
        return checkIndex(readerIndex);
    }

    @Override
    public int writerIndex() {
        return 0;
    }

    @Override
    public ByteBuf writerIndex(int writerIndex) {
        return checkIndex(writerIndex);
    }

    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        checkIndex(readerIndex);
        checkIndex(writerIndex);
        return this;
    }

    @Override
    public int readableBytes() {
        return 0;
    }

    @Override
    public int writableBytes() {
        return 0;
    }

    @Override
    public int maxWritableBytes() {
        return 0;
    }

    @Override
    public boolean isReadable() {
        return false;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public ByteBuf clear() {
        return this;
    }

    @Override
    public ByteBuf markReaderIndex() {
        return this;
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return this;
    }

    @Override
    public ByteBuf markWriterIndex() {
        return this;
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return this;
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        if (minWritableBytes < 0) {
            throw new IllegalArgumentException("minWritableBytes: " + minWritableBytes + " (expected: >= 0)");
        }
        if (minWritableBytes != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this;
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return new ByteBuffer[]{EMPTY_BYTE_BUFFER};
    }


    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        if (minWritableBytes < 0) {
            throw new IllegalArgumentException("minWritableBytes: " + minWritableBytes + " (expected: >= 0)");
        }

        if (minWritableBytes == 0) {
            return 0;
        }

        return 1;
    }

    @Override
    public byte getByte(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf writeByte(int value) {
        return null;
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        return checkIndex(index, length);
    }

    @Override
    public byte readByte() {
        throw new IndexOutOfBoundsException();
    }


    @Override
    public ByteBuf writeZero(int length) {
        return checkLength(length);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        checkIndex(fromIndex);
        checkIndex(toIndex);
        return -1;
    }


    @Override
    public ByteBuf copy() {
        return this;
    }


    @Override
    public int nioBufferCount() {
        return 1;
    }

    @Override
    public ByteBuffer nioBuffer() {
        return EMPTY_BYTE_BUFFER;
    }

    @Override
    public boolean hasArray() {
        return true;
    }

    @Override
    public byte[] array() {
        return EmptyArrays.EMPTY_BYTES;
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return null;
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        checkIndex(index, length);
        return nioBuffer();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return new ByteBuffer[0];
    }

    @Override
    public int arrayOffset() {
        return 0;
    }

    @Override
    public boolean hasMemoryAddress() {
        return EMPTY_BYTE_BUFFER_ADDRESS != 0;
    }

    @Override
    public long memoryAddress() {
        if (hasMemoryAddress()) {
            return EMPTY_BYTE_BUFFER_ADDRESS;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ByteBuf && !((ByteBuf) obj).isReadable();
    }

    @Override
    public int compareTo(ByteBuf buffer) {
        return buffer.isReadable() ? -1 : 0;
    }

    @Override
    public String toString() {
        return str;
    }

    @Override
    public boolean isReadable(int size) {
        return false;
    }

    @Override
    public boolean isWritable(int size) {
        return false;
    }

    @Override
    public int refCnt() {
        return 1;
    }

    @Override
    public ByteBuf retain() {
        return this;
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return null;
    }

    @Override
    public ByteBuf retain(int increment) {
        return this;
    }

    @Override
    public boolean release() {
        return false;
    }

    @Override
    public boolean release(int decrement) {
        return false;
    }

    private ByteBuf checkIndex(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this;
    }

    private ByteBuf checkIndex(int index, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length: " + length);
        }
        if (index != 0 || length != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this;
    }

    private ByteBuf checkLength(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length: " + length + " (expected: >= 0)");
        }
        if (length != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this;
    }
}

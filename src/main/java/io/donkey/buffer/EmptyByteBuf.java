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
    public ByteBuf discardReadBytes() {
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
    public ByteBuf getBytes(int index, byte[] dst) {
        return checkIndex(index, dst.length);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        return checkIndex(index, dst.remaining());
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        return checkIndex(index, length);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        return checkIndex(index, length);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        return checkIndex(index, src.length);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        return checkIndex(index, length);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        return checkIndex(index, src.remaining());
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
    public ByteBuf readBytes(int length) {
        return checkLength(length);
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        return checkLength(dst.length);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        return checkLength(dst.remaining());
    }

    @Override
    public ByteBuf skipBytes(int length) {
        return checkLength(length);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        return checkLength(src.length);
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        return checkLength(src.remaining());
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
    public int bytesBefore(byte value) {
        return -1;
    }

    @Override
    public int bytesBefore(int length, byte value) {
        checkLength(length);
        return -1;
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        checkIndex(index, length);
        return -1;
    }

    @Override
    public ByteBuf copy() {
        return this;
    }

    @Override
    public ByteBuf slice() {
        return this;
    }

    @Override
    public ByteBuf duplicate() {
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
    public String toString(Charset charset) {
        return "";
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

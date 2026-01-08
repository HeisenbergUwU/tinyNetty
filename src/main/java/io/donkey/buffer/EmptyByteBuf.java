package io.donkey.buffer;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

/**
 * 空 ByteBuf
 */
public final class EmptyByteBuf extends ByteBuf {

    private static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.allocate(0);
    private static final long EMPTY_BYTE_BUFFER_ADDRESS;
    private final ByteBufAllocator alloc;
    private final ByteOrder order;
    private final String str;

    public EmptyByteBuf() {
    }

    static {
        EMPTY_BYTE_BUFFER_ADDRESS = 0;
    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        return null;
    }

    @Override
    public int maxCapacity() {
        return 0;
    }

    @Override
    public ByteBufAllocator alloc() {
        return null;
    }

    @Override
    public int readerIndex() {
        return 0;
    }

    @Override
    public ByteBuf readerIndex(int readerIndex) {
        return null;
    }

    @Override
    public int writerIndex() {
        return 0;
    }

    @Override
    public ByteBuf writerIndex(int writerIndex) {
        return null;
    }

    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        return null;
    }

    @Override
    public boolean isWritable(int numBytes) {
        return false;
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
    public boolean isReadable(int size) {
        return false;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        return null;
    }

    @Override
    public ByteBuf clear() {
        return null;
    }

    @Override
    public ByteBuf markReaderIndex() {
        return null;
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return null;
    }

    @Override
    public ByteBuf markWriterIndex() {
        return null;
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return null;
    }

    @Override
    public ByteBuf discardReadBytes() {
        return null;
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        return null;
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return 0;
    }

    @Override
    public byte getByte(int index) {
        return 0;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        return null;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        return null;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        return null;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        return null;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        return null;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        return null;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        return null;
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        return null;
    }

    @Override
    public ByteBuf readBytes(int length) {
        return null;
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        return null;
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        return null;
    }

    @Override
    public ByteBuf skipBytes(int length) {
        return null;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        return null;
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        return null;
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        return null;
    }

    @Override
    public ByteBuf writeZero(int length) {
        return null;
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return 0;
    }

    @Override
    public int bytesBefore(byte value) {
        return 0;
    }

    @Override
    public int bytesBefore(int length, byte value) {
        return 0;
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        return 0;
    }

    @Override
    public ByteBuf copy() {
        return null;
    }

    @Override
    public ByteBuf slice() {
        return null;
    }

    @Override
    public ByteBuf duplicate() {
        return null;
    }

    @Override
    public int nioBufferCount() {
        return 0;
    }

    @Override
    public ByteBuffer nioBuffer() {
        return null;
    }

    @Override
    public boolean hasArray() {
        return false;
    }

    @Override
    public byte[] array() {
        return new byte[0];
    }

    @Override
    public int arrayOffset() {
        return 0;
    }

    @Override
    public boolean hasMemoryAddress() {
        return false;
    }

    @Override
    public long memoryAddress() {
        return 0;
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
        return false;
    }

    @Override
    public int compareTo(ByteBuf buffer) {
        return 0;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public ByteBuf retain(int increment) {
        return null;
    }

    @Override
    public boolean release() {
        return false;
    }

    @Override
    public boolean release(int decrement) {
        return false;
    }

    @Override
    public int refCnt() {
        return 0;
    }

    @Override
    public ByteBuf retain() {
        return null;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        return null;
    }

    @Override
    public byte readByte() {
        return 0;
    }
}
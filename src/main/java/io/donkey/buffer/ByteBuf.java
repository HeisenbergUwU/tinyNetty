package io.donkey.buffer;


import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * All in One ByteBuf with Allocator, crews are in heap.
 */
public class ByteBuf {
    public int capacity = 64;
    private byte[] array = new byte[capacity];
    private final AtomicLong refCnt = new AtomicLong(1);
    private static final int MAX_EXPANSION = 4096;

    public int readIndex;
    public int writeIndex;

    public byte readByte() {
        if (readIndex < writeIndex) {
            return this.array[readIndex++];
        } else {
            throw new RuntimeException("ReadByte OverFlow..");
        }
    }

    public void writeByte(byte b) {
        if (writeIndex >= capacity) {
            expansion();
        }
        array[writeIndex++] = b;
    }

    private void expansion() {
        int size = Math.min(MAX_EXPANSION, capacity << 1);
        byte[] newArray;
        if (capacity << 1 >= MAX_EXPANSION) {
            newArray = new byte[size + array.length];
        } else {
            newArray = new byte[size];
        }
        System.arraycopy(array, 0, newArray, 0, array.length);
        array = newArray;
        capacity = array.length;
    }

    public ByteBuffer wrap() {
        return ByteBuffer.wrap(array);
    }


    // 引用
    public long retain() {
        return refCnt.incrementAndGet();
    }

    public long release() {
        return refCnt.decrementAndGet();
    }

    public long refCnt() {
        return refCnt.get();
    }

}

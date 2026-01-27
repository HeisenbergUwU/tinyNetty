package io.donkey.buffer;


import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * All in One ByteBuf with Allocator, crews are in heap.
 */
public class ByteBuf {
    public int capacity = 64;
    private byte[] array = new byte[capacity];
    public AtomicLong refCnt = new AtomicLong();

    public int readIndex;
    public int writeIndex;

    public byte readByte() {
        if (readIndex < writeIndex) {
            readIndex += 1;
            return this.array[readIndex - 1];
        } else {
            throw new RuntimeException("ReadByte OverFlow..");
        }
    }

    public boolean writeByte(byte b) {
        if ()
    }

    private

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

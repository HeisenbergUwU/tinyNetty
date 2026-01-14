package io.donkey.buffer;

import java.nio.ByteBuffer;

/**
 * Big endian Java heap buffer implementation.
 */
public class UnpooledHeapByteBuf extends AbstractReferenceCountedByteBuf {

    private final ByteBufAllocator alloc;
    byte[] array;
    private ByteBuffer tmpNioBuf;

    protected UnpooledHeapByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
        this(alloc, new byte[initialCapacity], 0, 0, maxCapacity);
    }

    protected UnpooledHeapByteBuf(ByteBufAllocator alloc, byte[] initialArray, int maxCapacity) {
        this(alloc, initialArray, 0, initialArray.length, maxCapacity);
    }

    private UnpooledHeapByteBuf(
            ByteBufAllocator alloc, byte[] initialArray, int readerIndex, int writerIndex, int maxCapacity) {

        super(maxCapacity);
        if (alloc == null) {
            throw new NullPointerException("alloc");
        }
        if (initialArray == null) {
            throw new NullPointerException("initialArray");
        }
        if (initialArray.length > maxCapacity) {
            throw new IllegalArgumentException(String.format(
                    "initialCapacity(%d) > maxCapacity(%d)", initialArray.length, maxCapacity));
        }

        this.alloc = alloc;
        setArray(initialArray);
        setIndex(readerIndex, writerIndex);
    }

    private void setArray(byte[] initialArray) {
        array = initialArray;
        tmpNioBuf = null;
    }

    @Override
    public ByteBufAllocator alloc() {
        return alloc;
    }


    @Override
    public int nioBufferCount() {
        return 1;
    }


    @Override
    public int capacity() {
        ensureAccessible();
        return array.length;
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        ensureAccessible();
        if (newCapacity < 0 || newCapacity > maxCapacity()) {
            throw new IllegalArgumentException("newCapacity: " + newCapacity);
        }
        int oldCapacity = array.length;
        // 扩容
        if (newCapacity > oldCapacity) {
            byte[] newArray = new byte[newCapacity];
            System.arraycopy(array, 0, newArray, 0, array.length); // 创建一个新的数组
            setArray(newArray);
        } else if (newCapacity < oldCapacity) { // 缩容
            byte[] newArray = new byte[newCapacity];
            int readerIndex = readerIndex();
            // 读索引在新的容量范围内
            if (readerIndex < newCapacity) {
                int writerIndex = writerIndex();
                // 写索引在新容量范围外
                if (writerIndex > newCapacity) {
                    writerIndex(writerIndex = newCapacity);
                }
                System.arraycopy(array, readerIndex, newArray, readerIndex, writerIndex - readerIndex);
            } else {
                // 读索引超过了新容量
                setIndex(newCapacity, newCapacity); // 重置读写索引
            }
            setArray(newArray);
        }
        return this;
    }


    @Override
    public boolean hasArray() {
        return true;
    }

    @Override
    public byte[] array() {
        ensureAccessible();
        return array;
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        checkIndex(index, length);
        return (ByteBuffer) internalNioBuffer().clear().position(index).limit(index + length);
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        ensureAccessible();
        byte[] array = {1, 2, 3, 4, 5};
//        ByteBuffer wrap = ByteBuffer.wrap(array, 0, array.length);
//        ByteBuffer slice = wrap.slice();
//
//        System.out.println(wrap);
//        System.out.println(slice);
//        System.out.println(wrap == slice);
//        System.out.println(wrap.equals(slice));
        return ByteBuffer.wrap(array, index, length).slice();
    }


    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return new ByteBuffer[]{nioBuffer(index, length)};
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
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public ByteBuf copy(int index, int length) {
        checkIndex(index, length);
        byte[] copiedArray = new byte[length];
        System.arraycopy(array, index, copiedArray, 0, length);
        return new UnpooledHeapByteBuf(alloc(), copiedArray, maxCapacity());
    }


    @Override
    protected void deallocate() {
        array = null;
    }

    @Override
    protected void _setByte(int index, int value) {
        HeapByteBufUtil.setByte(array, index, value);
    }
    @Override
    protected byte _getByte(int index) {
        return 0;
    }

    private ByteBuffer internalNioBuffer() {
        ByteBuffer tmpNioBuf = this.tmpNioBuf;
        if (tmpNioBuf == null) {
            this.tmpNioBuf = tmpNioBuf = ByteBuffer.wrap(array);
        }
        return tmpNioBuf;
    }
}

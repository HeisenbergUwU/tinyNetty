package io.donkey.buffer;

import io.donkey.util.internal.StringUtil;

public abstract class AbstractByteBufAllocator implements ByteBufAllocator {
    private static final int DEFAULT_INITIAL_CAPACITY = 256;
    private static final int DEFAULT_MAX_COMPONENTS = 16;

    private final boolean directByDefault = false;
    private final ByteBuf emptyBuf;

    // Always Heap Memory
    protected AbstractByteBufAllocator() {
        emptyBuf = new EmptyByteBuf();
    }


    @Override
    public ByteBuf buffer() {
        return heapBuffer();
    }

    @Override
    public ByteBuf buffer(int initialCapacity) {
        return heapBuffer(initialCapacity);
    }

    @Override
    public ByteBuf buffer(int initialCapacity, int maxCapacity) {
        return heapBuffer(initialCapacity, maxCapacity);
    }

    @Override
    public boolean isDirectBufferPooled() {
        return false;
    }

    @Override
    public ByteBuf heapBuffer() {
        return heapBuffer(DEFAULT_INITIAL_CAPACITY, Integer.MAX_VALUE);
    }

    @Override
    public ByteBuf heapBuffer(int initialCapacity) {
        return heapBuffer(initialCapacity, Integer.MAX_VALUE);
    }

    @Override
    public ByteBuf heapBuffer(int initialCapacity, int maxCapacity) {
        if (initialCapacity == 0 && maxCapacity == 0) {
            return emptyBuf;
        }
        validate(initialCapacity, maxCapacity);
        return newHeapBuffer(initialCapacity, maxCapacity);
    }


    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "(directByDefault: " + directByDefault + ')';
    }

    private static void validate(int initialCapacity, int maxCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity: " + initialCapacity + " (expectd: 0+)");
        }
        if (initialCapacity > maxCapacity) {
            throw new IllegalArgumentException(String.format(
                    "initialCapacity: %d (expected: not greater than maxCapacity(%d)",
                    initialCapacity, maxCapacity));
        }
    }

    protected abstract ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity);
}
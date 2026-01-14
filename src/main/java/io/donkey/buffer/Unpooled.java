package io.donkey.buffer;

public final class Unpooled {
    private static final ByteBufAllocator ALLOC = UnpooledByteBufAllocator.DEFAULT;

    public static final ByteBuf EMPTY_BUFFER = ALLOC.buffer(0, 0);

    public static ByteBuf buffer() {
        return ALLOC.heapBuffer();
    }

    public static ByteBuf buffer(int initialCapacity) {
        return ALLOC.heapBuffer(initialCapacity);
    }

    public static ByteBuf buffer(int initialCapacity, int maxCapacity) {
        return ALLOC.heapBuffer(initialCapacity, maxCapacity);
    }
    
    public static ByteBuf wrappedBuffer(byte[] array) {
        return new UnpooledHeapByteBuf(ALLOC, array, array.length);
    }
}
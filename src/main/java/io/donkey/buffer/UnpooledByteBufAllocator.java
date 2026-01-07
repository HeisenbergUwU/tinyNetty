package io.donkey.buffer;

public final class UnpooledByteBufAllocator extends AbstractByteBufAllocator {

    public static final UnpooledByteBufAllocator DEFAULT = new UnpooledByteBufAllocator();



    @Override
    protected ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity) {
        return null;
    }
}

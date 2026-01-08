package io.donkey.buffer;

public final class UnpooledByteBufAllocator extends AbstractByteBufAllocator {

    public static final UnpooledByteBufAllocator DEFAULT = new UnpooledByteBufAllocator();

    public UnpooledByteBufAllocator() {
        super();
    }


    @Override
    protected ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity) {
        return null;
    }

}

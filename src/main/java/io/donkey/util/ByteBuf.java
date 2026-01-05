package io.donkey.util;

public abstract class ByteBuf implements ReferenceCounted, Comparable<ByteBuf> {

    public abstract int capacity();

    public abstract ByteBuf capacity(int newCapacity);

    public abstract int maxCapacity();

    public abstract ByteBufAllocator alloc();


}

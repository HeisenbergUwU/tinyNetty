package io.donkey.buffer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractByteBuf extends ByteBuf {
    private static final boolean checkAccessible = true;

    int readerIndex;
    int writerIndex;
    private int markedReaderIndex;
    private int markedWriterIndex;
}
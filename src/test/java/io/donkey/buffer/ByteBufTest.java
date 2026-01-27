package io.donkey.buffer;

import org.junit.Test;

import java.nio.ByteBuffer;

public class ByteBufTest {
    @Test
    public void testWrite() {
        ByteBuf byteBuf = new ByteBuf();
        for (int i = 0; i < 10000; i++) {
            byteBuf.writeByte((byte) i);
        }
        System.out.println(byteBuf.capacity);
    }

    @Test
    public void testRead() {
        ByteBuf byteBuf = new ByteBuf();
        for (int i = 0; i < 10; i++) {
            byteBuf.writeByte((byte) i);
        }
        System.out.println(byteBuf.capacity);

        while (byteBuf.readIndex < byteBuf.writeIndex) {
            System.out.println(byteBuf.readByte());
        }
    }

    @Test
    public void testWrap() {
        ByteBuf byteBuf = new ByteBuf();
        for (int i = 0; i < 10000; i++) {
            byteBuf.writeByte((byte) i);
        }
        ByteBuffer wrap = byteBuf.wrap();
        System.out.println(wrap);
    }
}

package io.donkey.buffer;

import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class testUnpooledHeapByteBuf {
    @Test
    public void testToString() {
        ByteBuf buffer = Unpooled.buffer();
        System.out.println(buffer.toString());
        String s = "Hello World.";
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < bytes.length; i++) {
            buffer.writeByte(bytes[i]);
        }
        System.out.println(buffer.toString());
    }
}

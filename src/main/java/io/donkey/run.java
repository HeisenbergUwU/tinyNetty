package io.donkey;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

public class run {

    private static void copyTest() {
        byte[] array = {1, 2, 3, 4, 5};
        ByteBuffer wrap = ByteBuffer.wrap(array, 0, array.length);
        ByteBuffer slice = wrap.slice();

        System.out.println(wrap);
        System.out.println(slice);
        System.out.println(wrap == slice);
        System.out.println(wrap.equals(slice));
    }

    private static void byteBufferTest() {
        ByteBuffer allocate = ByteBuffer.allocate(42);
        ByteBuffer put = allocate.put((byte) -128);
        System.out.printf("position: %d, limit: %d, capacity: %d.\n", put.position(), put.limit(), put.capacity());
        put.flip();
        System.out.printf("position: %d, limit: %d, capacity: %d.\n", put.position(), put.limit(), put.capacity());
        byte b = put.get();
        System.out.println("After get byte: " + b);
        System.out.printf("position: %d, limit: %d, capacity: %d.\n", put.position(), put.limit(), put.capacity());
    }

    private static int getIntFromByteArray(byte[] memory, int index) {
        return (memory[index] & 0xff) << 24 |
                (memory[index + 1] & 0xff) << 16 |
                (memory[index + 2] & 0xff) << 8 |
                memory[index + 3] & 0xff;
    }

    public static void main(String[] args) throws CharacterCodingException {
        int intFromByteArray = getIntFromByteArray(new byte[]{
                1, 2, 3, 4
        }, 0);

        System.out.println(intFromByteArray);
    }
}

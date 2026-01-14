package io.donkey;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

public class run {
    public static void main(String[] args) throws CharacterCodingException {
        byte[] array = {1, 2, 3, 4, 5};
        ByteBuffer wrap = ByteBuffer.wrap(array, 0, array.length);
        ByteBuffer slice = wrap.slice();

        System.out.println(wrap);
        System.out.println(slice);
        System.out.println(wrap == slice);
        System.out.println(wrap.equals(slice));
    }
}

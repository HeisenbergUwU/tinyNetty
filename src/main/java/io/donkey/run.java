package io.donkey;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

public class run {
    public static void main(String[] args) throws CharacterCodingException {

        Charset utf8 = StandardCharsets.UTF_8;

        CharsetEncoder encoder = utf8.newEncoder();
        System.out.println(encoder.maxBytesPerChar());
        ByteBuffer hello = encoder.encode(CharBuffer.wrap("Hello"));
        encoder.flush(hello);
        hello.clear();
        int position = hello.position();
        System.out.println(position);
        int capacity = hello.capacity();
        System.out.println(capacity);
        int limit = hello.limit();
        System.out.println(limit);

        CharBuffer dst = CharBuffer.allocate(1024);
        int length = dst.length();

        System.out.println(length);
    }
}

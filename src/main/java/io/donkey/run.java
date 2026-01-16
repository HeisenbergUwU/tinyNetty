package io.donkey;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    private static void dp() {
        String[] strings = {"hello", "world"};
    }

    class ChatBot {
        private static HashMap history = new HashMap<Integer, String>();

        ChatBot() {
            // ...
        }

        protected String chat(Integer times, String echo) {
            history.put(times, echo);
            return echo;
        }
    }

    static public int climbStairs(int n) {
        if (n <= 1) {
            return 1;
        }

        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = 1;

        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }

        return dp[n];
    }

    public static void main(String[] args) {

        System.out.println(climbStairs(5));
    }
}

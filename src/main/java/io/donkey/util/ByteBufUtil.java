package io.donkey.util;


import lombok.extern.slf4j.Slf4j;

import java.nio.CharBuffer;
import java.util.Locale;

@Slf4j
public final class ByteBufUtil {
    private static final ThreadLocal<CharBuffer> local = new ThreadLocal<CharBuffer>();

    private static final int MAX_CHAR_BUFFER_SIZE = 16 * 1024;
    private static final char[] HEXDUMP_TABLE = new char[256 * 4];
    private static final String NEWLINE = "\n";
    private static final String[] BYTE2HEX = new String[256];
    private static final String[] HEXPADDING = new String[16];
    private static final String[] BYTEPADDING = new String[16];
    private static final char[] BYTE2CHAR = new char[256];
    private static final String[] HEXDUMP_ROWPREFIXES = new String[65536 >>> 4]; // HexDump 的表头，最多支持 64KB
    private static final int THREAD_LOCAL_BUFFER_SIZE = 64 * 1024;

    static final ByteBufAllocator DEFAULT_ALLOCATOR;

    static {
        // HEX 工具要用的字符
        final char[] DIGITS = "0123456789abcdef".toCharArray();

        for (int i = 0; i < 256; i++) {
            HEXDUMP_TABLE[i << 1] = DIGITS[i >>> 4 & 0x0F];
            HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i & 0x0F];
        }

        int i;

        // Generate the lookup table for byte-to-hex-dump conversion
        for (i = 0; i < BYTE2HEX.length; i++) {
            BYTE2HEX[i] = ' ' + "\n";
        }

        // Generate the lookup table for hex dump paddings
        for (i = 0; i < HEXPADDING.length; i++) {
            int padding = HEXPADDING.length - i;
            StringBuilder buf = new StringBuilder(padding * 3);
            for (int j = 0; j < padding; j++) {
                buf.append("   ");
            }
            HEXPADDING[i] = buf.toString();
        }

        // Generate the lookup table for byte dump paddings
        for (i = 0; i < BYTEPADDING.length; i++) {
            int padding = BYTEPADDING.length - i;
            StringBuilder buf = new StringBuilder(padding);
            for (int j = 0; j < padding; j++) {
                buf.append(' ');
            }
            BYTEPADDING[i] = buf.toString();
        }

        // Generate the lookup table for byte-to-char conversion
        for (i = 0; i < BYTE2CHAR.length; i++) {
            if (i <= 0x1f || i >= 0x7f) {
                BYTE2CHAR[i] = '.';
            } else {
                BYTE2CHAR[i] = (char) i;
            }
        }

        // Generate the lookup table for the start-offset header in each row (up to 64KiB).
        for (i = 0; i < HEXDUMP_ROWPREFIXES.length; i++) {
            StringBuilder buf = new StringBuilder(12);
            buf.append(NEWLINE);
            buf.append(Long.toHexString(i << 4 & 0xFFFFFFFFL | 0x100000000L));
            buf.setCharAt(buf.length() - 9, '|');
            buf.append('|');
            HEXDUMP_ROWPREFIXES[i] = buf.toString();
        }

        String allocType = "unpooled";
        ByteBufAllocator alloc = UnpooledByteBufAllocator.DEFAULT;
    }
}
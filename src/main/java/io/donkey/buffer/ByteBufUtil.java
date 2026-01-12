package io.donkey.buffer;


import io.donkey.util.CharsetUtil;
import io.donkey.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

@Slf4j
public final class ByteBufUtil {
    private static final ThreadLocal<CharBuffer> local = new ThreadLocal<CharBuffer>();

    private static final int MAX_CHAR_BUFFER_SIZE = 16 * 1024;
    private static final char[] HEXDUMP_TABLE = new char[256 * 4];
    private static final String NEWLINE = StringUtil.NEWLINE;
    private static final String[] BYTE2HEX = new String[256]; // 字节 -> Hex 编码
    private static final String[] HEXPADDING = new String[16];
    private static final String[] BYTEPADDING = new String[16];
    private static final char[] BYTE2CHAR = new char[256];
    private static final String[] HEXDUMP_ROWPREFIXES = new String[65536 >>> 4]; // HexDump 的表头，最多支持 64KB
    private static final int THREAD_LOCAL_BUFFER_SIZE = 64 * 1024;
    private static final String[] BYTE2HEX_PAD = new String[256];
    public static final ByteBufAllocator DEFAULT_ALLOCATOR = null;

    static {
        // HEX 工具要用的字符
        final char[] DIGITS = "0123456789abcdef".toCharArray();
//    00: 00 01: 01 02: 02 03: 03 04: 04 05: 05 06: 06 07: 07 08: 08 09: 09 0a: 0a 0b: 0b 0c: 0c 0d: 0d 0e: 0e 0f: 0f
//    10: 10 11: 11 12: 12 13: 13 14: 14 15: 15 16: 16 17: 17 18: 18 19: 19 1a: 1a 1b: 1b 1c: 1c 1d: 1d 1e: 1e 1f: 1f
//    ...
        for (int i = 0; i < 256; i++) {
            HEXDUMP_TABLE[i << 1] = DIGITS[i >>> 4 & 0x0F]; // i >>> 4 保留符号，整除 16
            HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i & 0x0F]; // i & 0x0F 除 16 取 余数
        }

        int i;

        // Generate the lookup table for byte-to-hex-dump conversion
        for (i = 0; i < BYTE2HEX.length; i++) {
            BYTE2HEX[i] = ' ' + StringUtil.byteToHexStringPadded(i);
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

    public static String byteToHexStringPadded(int value) {
        return BYTE2HEX_PAD[value & 0xff];
    }

    public static String hexDump(ByteBuf buffer) {
        return hexDump(buffer, buffer.readerIndex(), buffer.readableBytes());
    }

    public static String hexDump(ByteBuf buffer, int fromIndex, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length: " + length);
        }
        if (length == 0) {
            return "";
        }

        int endIndex = fromIndex + length;
        char[] buf = new char[length << 1];

        int srcIdx = fromIndex;
        int dstIdx = 0;
        for (; srcIdx < endIndex; srcIdx++, dstIdx += 2) {
            System.arraycopy(
                    HEXDUMP_TABLE, buffer.getByte(srcIdx) & 0xff,
                    buf, dstIdx, 2);
        }

        return new String(buf);
    }

    public static int hashCode(ByteBuf buffer) {
        final int aLen = buffer.readableBytes();
        final int byteCount = aLen;

        int hashCode = 1;
        int arrayIndex = buffer.readerIndex();

        for (int i = byteCount; i > 0; i--) {
            hashCode = 31 * hashCode + buffer.getByte(arrayIndex++);
        }

        if (hashCode == 0) {
            hashCode = 1;
        }

        return hashCode;
    }

    public static boolean equals(ByteBuf bufferA, ByteBuf bufferB) {
        final int aLen = bufferA.readableBytes();
        if (aLen != bufferB.readableBytes()) {
            return false;
        }

        final int longCount = aLen;
        final int byteCount = aLen;

        int aIndex = bufferA.readerIndex();
        int bIndex = bufferB.readerIndex();

        for (int i = longCount; i > 0; i--) {
            if (bufferA.getByte(aIndex) != bufferB.getByte(bIndex)) {
                return false;
            }
            aIndex += 1;
            bIndex += 1;
        }
        return true;
    }

    public static int compare(ByteBuf bufferA, ByteBuf bufferB) {
        final int aLen = bufferA.readableBytes();
        final int bLen = bufferB.readableBytes();
        final int minLength = Math.min(aLen, bLen);

        int aIndex = bufferA.readerIndex();
        int bIndex = bufferB.readerIndex();

        for (int i = minLength; i > 0; i--) {
            short va = (short) (bufferA.getByte(aIndex) & 0xff);
            short vb = (short) (bufferB.getByte(bIndex) & 0xff);
            if (va > vb) {
                return 1;
            }
            if (va < vb) {
                return -1;
            }
            aIndex++;
            bIndex++;
        }
        return aLen - bLen;
    }

    public static int indexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) {
        if (fromIndex <= toIndex) {
            return firstIndexOf(buffer, fromIndex, toIndex, value);
        } else {
            return lastIndexOf(buffer, fromIndex, toIndex, value);
        }
    }

    public static int writeUtf8(ByteBuf buf, CharSequence seq) {
        if (buf == null) {
            throw new NullPointerException("buf");
        }
        if (seq == null) {
            throw new NullPointerException("seq");
        }
        // UTF-8 uses max. 3 bytes per char, so calculate the worst case.
        final int len = seq.length();
        final int maxSize = len * 3;
        buf.ensureWritable(maxSize);

        for (; ; ) {
            if (buf instanceof AbstractByteBuf) {
                return writeUtf8((AbstractByteBuf) buf, seq, len);
            } else {
                byte[] bytes = seq.toString().getBytes(CharsetUtil.UTF_8);
                for (int i = 0; i < bytes.length; i++) {
                    buf.writeByte(bytes[i]);
                }
                return bytes.length;
            }
        }
    }

    // Fast-Path implementation
    private static int writeUtf8(AbstractByteBuf buffer, CharSequence seq, int len) {
        int oldWriterIndex = buffer.writerIndex;
        int writerIndex = oldWriterIndex;

        // We can use the _set methods as these not need to do any index checks and reference checks.
        // This is possible as we called ensureWritable(...) before.
        for (int i = 0; i < len; i++) {
            char c = seq.charAt(i);
            if (c < 0x80) {
                buffer._setByte(writerIndex++, (byte) c);
            } else if (c < 0x800) {
                buffer._setByte(writerIndex++, (byte) (0xc0 | (c >> 6)));
                buffer._setByte(writerIndex++, (byte) (0x80 | (c & 0x3f)));
            } else {
                buffer._setByte(writerIndex++, (byte) (0xe0 | (c >> 12)));
                buffer._setByte(writerIndex++, (byte) (0x80 | ((c >> 6) & 0x3f)));
                buffer._setByte(writerIndex++, (byte) (0x80 | (c & 0x3f)));
            }
        }
        // update the writerIndex without any extra checks for performance reasons
        buffer.writerIndex = writerIndex;
        return writerIndex - oldWriterIndex;
    }

    public static int writeAscii(ByteBuf buf, CharSequence seq) {
        if (buf == null) {
            throw new NullPointerException("buf");
        }
        if (seq == null) {
            throw new NullPointerException("seq");
        }
        // ASCII uses 1 byte per char
        final int len = seq.length();
        buf.ensureWritable(len);
        for (; ; ) {
            if (buf instanceof AbstractByteBuf) {
                writeAscii((AbstractByteBuf) buf, seq, len);
                break;
            } else {
                for (byte aByte : seq.toString().getBytes(CharsetUtil.US_ASCII)) {
                    buf.writeByte(aByte);
                }
                return len;
            }
        }
        return len;
    }

    private static void writeAscii(AbstractByteBuf buffer, CharSequence seq, int len) {
        int writerIndex = buffer.writerIndex;

        // We can use the _set methods as these not need to do any index checks and reference checks.
        // This is possible as we called ensureWritable(...) before.
        for (int i = 0; i < len; i++) {
            buffer._setByte(writerIndex++, (byte) seq.charAt(i));
        }
        // update the writerIndex without any extra checks for performance reasons
        buffer.writerIndex = writerIndex;
    }


    private static void decodeString(CharsetDecoder decoder, ByteBuffer src, CharBuffer dst) {
        try {
            CoderResult cr = decoder.decode(src, dst, true);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
            cr = decoder.flush(dst);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
        } catch (CharacterCodingException x) {
            throw new IllegalStateException(x);
        }
    }

    public static ByteBuf encodeString(ByteBufAllocator alloc, CharBuffer src, Charset charset) {
        return encodeString0(alloc, false, src, charset);
    }

    static ByteBuf encodeString0(ByteBufAllocator alloc, boolean enforceHeap, CharBuffer src, Charset charset) {
        final CharsetEncoder encoder = CharsetUtil.getEncoder(charset);
        int length = (int) ((double) src.remaining() * encoder.maxBytesPerChar());
        boolean release = true;
        final ByteBuf dst;
        if (enforceHeap) {
            dst = alloc.heapBuffer(length);
        } else {
            dst = alloc.buffer(length);
        }
        try {
            final ByteBuffer dstBuf = dst.internalNioBuffer(0, length);
            final int pos = dstBuf.position();
            CoderResult cr = encoder.encode(src, dstBuf, true);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
            cr = encoder.flush(dstBuf);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
            dst.writerIndex(dst.writerIndex() + dstBuf.position() - pos);
            release = false;
            return dst;
        } catch (CharacterCodingException x) {
            throw new IllegalStateException(x);
        } finally {
            if (release) {
                dst.release();
            }
        }
    }

    private static int firstIndexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) {
        fromIndex = Math.max(fromIndex, 0);
        if (fromIndex >= toIndex || buffer.capacity() == 0) {
            return -1;
        }
        for (int i = fromIndex; i < toIndex; i++) {
            byte aByte = buffer.getByte(i);
            if (aByte == value)
                return i;
        }
        return -1;
    }

    private static int lastIndexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) {
        fromIndex = Math.min(fromIndex, buffer.capacity() - 1);
        if (fromIndex < 0 || fromIndex <= toIndex || buffer.capacity() == 0) {
            return -1;
        }
        for (int i = fromIndex; i > toIndex; i--) {
            if (buffer.getByte(i) == value) {
                return i;
            }
        }
        return -1;
    }

}
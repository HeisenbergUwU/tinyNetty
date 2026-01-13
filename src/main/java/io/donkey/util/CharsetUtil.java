/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.donkey.util;


import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.IdentityHashMap;
import java.util.Map;


public final class CharsetUtil {

    public static final Charset UTF_8 = Charset.forName("UTF-8");

    public static final Charset US_ASCII = Charset.forName("US-ASCII");

    Map<Charset, CharsetEncoder> charsetEncoderCache;

    Map<Charset, CharsetDecoder> charsetDecoderCache;

    public Map<Charset, CharsetDecoder> charsetDecoderCache() {
        Map<Charset, CharsetDecoder> cache = charsetDecoderCache;
        if (cache == null) {
            charsetDecoderCache = cache = new IdentityHashMap<Charset, CharsetDecoder>();
        }
        return cache;
    }

    /**
     * Only UTF-8
     */
    public static CharsetEncoder getEncoder() {

        CharsetEncoder e = UTF_8.newEncoder();
        e.onMalformedInput(CodingErrorAction.REPLACE);
        e.onUnmappableCharacter(CodingErrorAction.REPLACE);
        return e;
    }

    /**
     * Only UTF-8
     */
    public static CharsetDecoder getDecoder() {

        CharsetDecoder d = UTF_8.newDecoder();
        d.onMalformedInput(CodingErrorAction.REPLACE);
        d.onUnmappableCharacter(CodingErrorAction.REPLACE);
        return d;
    }

    private CharsetUtil() {
        // Unused
    }
}

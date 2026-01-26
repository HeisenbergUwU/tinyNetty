package io.donkey.util;

import io.donkey.common.UniqueName;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class Signal extends Error {

    private static final long serialVersionUID = -221145131122459977L;

    private static final ConcurrentMap<String, Boolean> map = new ConcurrentHashMap<>();

    @SuppressWarnings("deprecation")
    private final UniqueName uname;

    /**
     * Creates a new {@link Signal} with the specified {@code name}.
     */
    @SuppressWarnings("deprecation")
    public static Signal valueOf(String name) {
        return new Signal(name);
    }

    /**
     * @deprecated Use {@link #valueOf(String)} instead.
     */
    @Deprecated
    public Signal(String name) {
        super(name);
        uname = new UniqueName(map, name);
    }

    /**
     * Check if the given {@link Signal} is the same as this instance. If not an {@link IllegalStateException} will
     * be thrown.
     */
    public void expect(Signal signal) {
        if (this != signal) {
            throw new IllegalStateException("unexpected signal: " + signal);
        }
    }

    @Override
    public Throwable initCause(Throwable cause) {
        return this;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public String toString() {
        return uname.name();
    }
}

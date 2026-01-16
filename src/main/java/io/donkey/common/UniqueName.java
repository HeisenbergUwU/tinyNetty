package io.donkey.common;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UniqueName implements Comparable<UniqueName> {

    private static final AtomicInteger nextId = new AtomicInteger();

    private final int id;
    private final String name;

    public static <T> T checkNotNull(T arg, String text) {
        if (arg == null) {
            throw new NullPointerException(text);
        }
        return arg;
    }

    public UniqueName(ConcurrentMap<String, Boolean> map, String name, Object... args) {
        checkNotNull(map, "map");

        if (args != null && args.length > 0) {
            validateArgs(args);
        }

        if (map.putIfAbsent(name, Boolean.TRUE) != null) {
            throw new IllegalArgumentException(String.format("'%s' is already in use", name));
        }
        this.name = checkNotNull(name, "name");
        id = nextId.incrementAndGet();
    }

    protected UniqueName(String name) {
        this.name = checkNotNull(name, "name");
        id = nextId.incrementAndGet();
    }

    @SuppressWarnings("unused")
    protected void validateArgs(Object... args) {
        // Subclasses will override.
    }

    public final String name() {
        return name;
    }

    public final int id() {
        return id;
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int compareTo(UniqueName other) {
        if (this == other) {
            return 0;
        }

        int returnCode = name.compareTo(other.name);
        if (returnCode != 0) {
            return returnCode;
        }

        return ((Integer) id).compareTo(other.id);
    }

    @Override
    public String toString() {
        return name();
    }
}

package io.donkey.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class AttributeKey<T> extends UniqueName {

    private static final ConcurrentMap<String, AttributeKey> names = new ConcurrentHashMap<>();

    public static <T> AttributeKey<T> valueOf(String name) {
        checkNotNull(name, "name");
        AttributeKey<T> option = names.get(name);
        if (option == null) {
            option = new AttributeKey<T>(name);
            AttributeKey<T> old = names.putIfAbsent(name, option);
            if (old != null) {
                option = old;
            }
        }
        return option;
    }

    public static boolean exists(String name) {
        checkNotNull(name, "name");
        return names.containsKey(name);
    }

    public static <T> AttributeKey<T> newInstance(String name) {
        checkNotNull(name, "name");
        AttributeKey<T> option = new AttributeKey<T>(name);
        AttributeKey<T> old = names.putIfAbsent(name, option);
        if (old != null) {
            throw new IllegalArgumentException(String.format("'%s' is already in use", name));
        }
        return option;
    }
    
    @Deprecated
    public AttributeKey(String name) {
        super(name);
    }
}

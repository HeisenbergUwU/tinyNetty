package io.donkey.common;

public interface AttributeMap {
    <T> Attribute<T> attr(AttributeKey<T> key);
}

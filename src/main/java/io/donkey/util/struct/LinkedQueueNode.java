package io.donkey.util.struct;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * 简单的队伍节点
 *
 * @param <T>
 */
public abstract class LinkedQueueNode<T> {

    private static final AtomicReferenceFieldUpdater<LinkedQueueNode, LinkedQueueNode> nextUpdater; // 这里是 ReferenceField，因此需要注意我们的 T 代表了对象，V 更新保护的字段

    static {
        nextUpdater = AtomicReferenceFieldUpdater.newUpdater(LinkedQueueNode.class, LinkedQueueNode.class, "next");
    }

    public volatile LinkedQueueNode<T> next;

    final LinkedQueueNode<T> next() {
        return next;
    }

    final void setNext(final LinkedQueueNode<T> newNext) {
        nextUpdater.lazySet(this, newNext);
    }

    public abstract T value();

    protected T clearMaybe() {
        return value();
    }

    void unlink() {
        setNext(null);
    }
}
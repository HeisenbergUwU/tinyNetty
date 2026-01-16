package io.donkey.concurrent;

import java.util.EventListener;

/**
 * EventListener 属于标记类型，用来判断是不是 事件监听器 的；JVM 或框架可通过 instanceof EventListener 快速识别“这是一个事件监听器”。
 * 类似的
 * - public interface Serializable {} ： ObjectOutputStream 在写入对象时会检查 obj instanceof Serializable。
 * - public interface Cloneable {}  ： Object.clone() 方法内部会检查 this instanceof Cloneable。
 * - public interface RandomAccess {} ： 作用：标记一个 List 实现支持高效的随机访问（即 list.get(i) 时间复杂度接近 O(1)）。
 * @param <F>
 */
public interface GenericFutureListener<F extends Future<?>> extends EventListener {

    void operationComplete(F future) throws Exception;
}
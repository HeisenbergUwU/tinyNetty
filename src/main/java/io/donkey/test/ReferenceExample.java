package io.donkey.test;

import java.lang.ref.*;

public class ReferenceExample {
    public static void main(String[] args) throws InterruptedException {
        // 1. 强引用（Strong Reference）
        Object strongObj = new Object();

        // 2. 软引用（Soft Reference）
        SoftReference<Object> softRef = new SoftReference<>(new Object());

        // 3. 弱引用（Weak Reference）
        WeakReference<Object> weakRef = new WeakReference<>(new Object());
        Object o = new Object();
        // 4. 虚引用（Phantom Reference）
        ReferenceQueue<Object> queue = new ReferenceQueue<>();
        PhantomReference<Object> phantomRef = new PhantomReference<>(o, queue);

        // 打印初始状态
        System.out.println("=== 初始状态 ===");
        System.out.println("Strong: " + (strongObj != null ? "存活" : "已回收"));
        System.out.println("Soft:   " + (softRef.get() != null ? "存活" : "已回收"));
        System.out.println("Weak:   " + (weakRef.get() != null ? "存活" : "已回收"));
        System.out.println("Phantom: " + (queue.poll() == null ? "无法获取（正常）" : "异常"));

        // 清除强引用（使其变为可回收）
        strongObj = null;
        o = null;
        // 主动触发 GC
        System.gc();
        Thread.sleep(200); // 等待 GC 完成

        System.out.println("\n=== GC 后 ===");
        System.out.println("Strong: " + (strongObj != null ? "存活" : "已回收")); // null
        System.out.println("Soft:   " + (softRef.get() != null ? "存活" : "已回收")); // 通常存活（内存充足）
        System.out.println("Weak:   " + (weakRef.get() != null ? "存活" : "已回收")); // 已回收！
        System.out.println("Phantom in queue: " + (queue.poll() != null ? "对象标记为死亡" : "失败"));

        // 模拟内存不足（软引用会被回收）
        System.out.println("\n=== 尝试制造内存压力 ===");
        try {
            // 分配大数组（可能触发软引用回收）
            byte[][] bigArray = new byte[1000][];
            for (int i = 0; i < 1000; i++) {
                bigArray[i] = new byte[1024 * 1024]; // 1MB
            }
        } catch (OutOfMemoryError ignored) {
        }
        System.gc();
        Thread.sleep(100);

        System.out.println("Soft after memory pressure: " +
                (softRef.get() != null ? "存活" : "已回收")); // 很可能已回收！
    }
}
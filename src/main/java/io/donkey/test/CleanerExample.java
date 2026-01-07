package io.donkey.test;

import java.lang.ref.Cleaner;

public class CleanerExample {

    static class OffHeapBuffer {
        private final long address;
        private final String name;
        private static final Cleaner cleaner = Cleaner.create();

        private static class CleanupAction implements Runnable {
            private long address;
            private String name;

            CleanupAction(long address, String name) {
                this.address = address;
                this.name = name;
            }

            @Override
            public void run() {
                System.out.println("【Cleaner 自动清理】释放堆外资源: " + name + ", 地址=" + address);
                // 实际项目中在这里调用 UNSAFE.freeMemory(address) 或 close(fd)
            }
        }

        public OffHeapBuffer(String name) {
            this.name = name;
            this.address = System.nanoTime(); // 模拟唯一地址
            // ✅ 正确注册：传入 this 和清理动作
            cleaner.register(this, new CleanupAction(this.address, this.name));
            System.out.println("创建 OffHeapBuffer: " + name + ", 地址=" + address);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 开始测试 Cleaner ===");

        OffHeapBuffer buffer = new OffHeapBuffer("buffer-1");

        // 移除强引用，使对象可被回收
        buffer = null;

        // 强制 GC（仅用于演示！）
        System.gc();
        Thread.sleep(200); // 等待 Cleaner 后台线程执行

        System.out.println("=== 测试结束 ===");
    }
}
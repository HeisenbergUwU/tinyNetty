package io.donkey.test;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;

public class PhantomReferenceExample {

    // 模拟一个持有堆外资源的对象
    static class Resource {
        private final String name;
        public Resource(String name) {
            this.name = name;
            System.out.println("创建 Resource: " + name);
        }
    }

    // 自定义虚引用，携带清理所需的信息（如资源ID、内存地址等）
    static class ResourcePhantomReference extends PhantomReference<Resource> {
        private final String resourceName;
        // 注意：不能持有 Resource 本身，否则会阻止 GC！

        public ResourcePhantomReference(Resource referent, ReferenceQueue<? super Resource> q, String name) {
            super(referent, q);
            this.resourceName = name;
        }

        public void cleanup() {
            System.out.println("【手动清理】释放资源: " + resourceName);
            // 在这里执行：free memory, close fd, unlink file, etc.
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReferenceQueue<Resource> queue = new ReferenceQueue<>();
        List<ResourcePhantomReference> refList = new ArrayList<>(); // 必须持有！防止 PhantomReference 自己被回收

        // 1. 创建资源对象
        Resource res = new Resource("resource-1");

        // 2. 创建虚引用，并强引用持有它（关键！）
        ResourcePhantomReference phantomRef = new ResourcePhantomReference(res, queue, "resource-1");
        refList.add(phantomRef);

        // 3. 清除对原对象的强引用
        res = null;

        System.out.println("触发 GC 前，poll = " + queue.poll()); // 通常为 null

        // 4. 强制 GC（仅用于演示！）
        System.gc();
        Thread.sleep(100); // 让 GC 和引用处理完成

        // 5. 检查引用队列
        Reference<?> poll = queue.poll();
        if (poll != null) {
            System.out.println("从队列中拿到 PhantomReference: " + poll.getClass().getSimpleName());

            // ✅ 重点：不能用 poll.get()（它永远返回 null）
            // 但可以强转为我们自己的类型！
            ResourcePhantomReference myRef = (ResourcePhantomReference) poll;
            myRef.cleanup(); // 执行清理

            refList.remove(myRef); // 可选：清理引用持有列表
        } else {
            System.out.println("队列为空，对象可能还未被回收（GC 时机不确定）");
        }
    }
}
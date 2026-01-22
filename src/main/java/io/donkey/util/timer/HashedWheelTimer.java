package io.donkey.util.timer;

import io.donkey.util.internal.StringUtil;
import io.donkey.util.struct.LinkedQueueNode;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * 这是一个单次触发模型，
 */
@Slf4j
public class HashedWheelTimer implements Timer {

    private static final AtomicIntegerFieldUpdater<HashedWheelTimer> WORKER_STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(HashedWheelTimer.class, "workerState");
    private final Worker worker = new Worker(); // 驱动时间轮
    private final Thread workerThread;
    private volatile long startTime;

    public static final int WORKER_STATE_INIT = 0;
    public static final int WORKER_STATE_STARTED = 1;
    public static final int WORKER_STATE_SHUTDOWN = 2;
    private volatile int workerState = WORKER_STATE_INIT;

    private final long tickDuration;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
    private final HashedWheelBucket[] wheel;
    private final int mask;
    private final CountDownLatch startTimeInitialized = new CountDownLatch(1);
    private final Queue<HashedWheelTimeout> timeouts = new ConcurrentLinkedQueue<>(); // 退化为 CAS 保证原子性
    private final Queue<Runnable> cancelledTimeouts = new ConcurrentLinkedQueue<>();

    // 没啥养分的重载构造方法
    public HashedWheelTimer() {
        this(Executors.defaultThreadFactory());
    }

    public HashedWheelTimer(long tickDuration, TimeUnit unit) {
        this(Executors.defaultThreadFactory(), tickDuration, unit);
    }

    public HashedWheelTimer(long tickDuration, TimeUnit unit, int ticksPerWheel) {
        this(Executors.defaultThreadFactory(), tickDuration, unit, ticksPerWheel);
    }

    public HashedWheelTimer(ThreadFactory threadFactory) {
        this(threadFactory, 100, TimeUnit.MILLISECONDS);
    }

    public HashedWheelTimer(
            ThreadFactory threadFactory, long tickDuration, TimeUnit unit) {
        this(threadFactory, tickDuration, unit, 512);
    }

    // 核心逻辑在这里面
    public HashedWheelTimer(
            ThreadFactory threadFactory,
            long tickDuration, TimeUnit unit, int ticksPerWheel) {
        if (threadFactory == null) {
            throw new NullPointerException("threadFactory");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        if (tickDuration <= 0) {
            throw new IllegalArgumentException("tickDuration must be greater than 0: " + tickDuration);
        }
        if (ticksPerWheel <= 0) {
            throw new IllegalArgumentException("ticksPerWheel must be greater than 0: " + ticksPerWheel);
        }

        wheel = createWheel(ticksPerWheel);
        mask = wheel.length - 1;

        this.tickDuration = unit.toNanos(tickDuration);

        if (this.tickDuration >= Long.MAX_VALUE / wheel.length) {
            throw new IllegalArgumentException(String.format(
                    "tickDuration: %d (expected: 0 < tickDuration in nanos < %d",
                    tickDuration, Long.MAX_VALUE / wheel.length));
        }
        workerThread = threadFactory.newThread(worker);

    }

    @Override
    public Timeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
        return null;
    }

    @Override
    public Set<Timeout> stop() {
        return Set.of();
    }

    private static HashedWheelBucket[] createWheel(int ticksPerWheel) {
        if (ticksPerWheel <= 0) {
            throw new IllegalArgumentException(
                    "ticksPerWheel must be greater than 0: " + ticksPerWheel);
        }
        if (ticksPerWheel > 1073741824) {
            throw new IllegalArgumentException(
                    "ticksPerWheel may not be greater than 2^30: " + ticksPerWheel);
        }

    }

    /**
     * ⚙️ 2. 计算机底层：为什么 & 比 % 快？
     * &（按位与） 是 CPU 最基础的逻辑运算指令，通常 1 个时钟周期就能完成。
     * %（取模） 本质上是 除法运算的副产品。而整数除法在 CPU 中是非常慢的操作（可能需要几十甚至上百个时钟周期，尤其在没有硬件除法器的架构上）。
     * 举个现实类比：
     *
     * & 就像“直接看最后几位数字”——一眼就知道。
     * % 就像“拿计算器做除法，再看余数”——步骤多、耗时长。
     * 在像 Netty 这种每秒处理成千上万定时任务的场景下，每次调度都要算槽位索引。如果用 %，性能会成为瓶颈；用 &，几乎无开销。
     * @param ticksPerWheel
     * @return
     */
    private static int normalizeTicksPerWheel(int ticksPerWheel) {
        int normalizedTicksPerWheel = 1;
        while (normalizedTicksPerWheel < ticksPerWheel) {
            normalizedTicksPerWheel <<= 1; // 比ticksPerWheel 打一个幂的 参数
        }
        return normalizedTicksPerWheel;
    }

    /**
     * 每个桶中装有好多 timeout，每一次 tiktok【例如 100ms】都会检查一个桶，这个桶保存的就是一个链表
     */
    private static final class HashedWheelBucket {
        private HashedWheelTimeout head;
        private HashedWheelTimeout tail;

        public void addTimeout(HashedWheelTimeout timeout) {
            assert timeout.bucket == null;
            timeout.bucket = this;
            if (head == null) {
                head = tail = timeout;
            } else {
                tail.next = timeout;
                timeout.prev = tail;
                tail = timeout;
            }
        }

        /*
         * Expire all {@link HashedWheelTimeout}s for the given {@code deadline}.
         */
        public void expireTimeouts(long deadline) {
            HashedWheelTimeout timeout = head;
            // 执行所有的 timeouts
            while (timeout != null) {
                boolean remove = false;
                if (timeout.remainingRounds <= 0) {
                    if (timeout.deadline <= deadline) {
                        timeout.expire();
                    } else {
                        // 出现了timeout 放入了错误的桶。严重错误
                        throw new IllegalStateException(String.format(
                                "timeout.deadline (%d) > deadline (%d)", timeout.deadline, deadline));
                    }
                    remove = true;
                } else if (timeout.isCancelled()) {
                    remove = true;
                } else {
                    timeout.remainingRounds--;
                }
                HashedWheelTimeout next = timeout.next;
                if (remove) {
                    remove(timeout);
                }
                timeout = next;
            }
        }

        public void remove(HashedWheelTimeout timeout) {
            HashedWheelTimeout next = timeout.next;
            // 清理执行过或取消的Node
            if (timeout.prev != null) {
                timeout.prev.next = next;
            }
            if (timeout.next != null) {
                timeout.next.prev = timeout.prev;
            }
            if (timeout == head) {
                if (timeout == tail) {
                    tail = null;
                    head = null;
                } else {
                    head = next;
                }
            } else if (timeout == tail) {
                tail = timeout.prev;
            }
            timeout.prev = null;
            timeout.next = null;
            timeout.bucket = null;
        }

        public void clearTimeouts(Set<Timeout> set) {
            for (; ; ) {
                HashedWheelTimeout timeout = pollTimeout();
                if (timeout == null) {
                    return;
                }
                if (timeout.isExpired() || timeout.isCancelled()) {
                    continue;
                }
                set.add(timeout);
            }
        }

        private HashedWheelTimeout pollTimeout() {
            HashedWheelTimeout head = this.head;
            if (head == null) {
                return null;
            }
            HashedWheelTimeout next = head.next;
            if (next == null) {
                tail = this.head = null;
            } else {
                this.head = next;
                next.prev = null;
            }

            head.next = null;
            head.prev = null;
            head.bucket = null;
            return head;
        }
    }

    private final class Worker implements Runnable {
        private final Set<Timeout> unprocessedTimeouts = new HashSet<Timeout>();

        private long tick;

        @Override
        public void run() {
            startTime = System.nanoTime();
        }

        private void transferTimeoutsToBuckets() {

        }

        private void processCancelledTasks() {
        }
    }

    @Slf4j
    private static final class HashedWheelTimeout extends LinkedQueueNode<Timeout> implements Timeout {
        private static final int ST_INIT = 0;
        private static final int ST_CANCELLED = 1;
        private static final int ST_EXPIRED = 2;
        private static final AtomicIntegerFieldUpdater<HashedWheelTimeout> STATE_UPDATER;

        static {
            STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(HashedWheelTimeout.class, "state"); // unsafe.compareAndSwapInt(obj, stateFieldOffset, expect, update);
        }

        private final HashedWheelTimer timer;
        private final TimerTask task;
        private final long deadline;
        long remainingRounds;

        // This will be used to chain timeouts in HashedWheelTimerBucket via a double-linked-list.
        // As only the workerThread will act on it there is no need for synchronization / volatile.
        HashedWheelTimeout next;
        HashedWheelTimeout prev;
        private volatile int state = ST_INIT;


        HashedWheelBucket bucket;

        HashedWheelTimeout(HashedWheelTimer timer, TimerTask task, long deadline) {
            this.timer = timer;
            this.task = task;
            this.deadline = deadline;
        }

        @Override
        public Timer timer() {
            return timer;
        }

        @Override
        public TimerTask task() {
            return task;
        }

        @Override
        public boolean cancel() {
            if (!compareAndSetState(ST_INIT, ST_CANCELLED)) {
                return false;
            }
            timer.cancelledTimeouts.add(new Runnable() {
                @Override
                public void run() {
                    HashedWheelBucket bucket = HashedWheelTimeout.this.bucket;
                    if (bucket != null) {
                        bucket.remove(HashedWheelTimeout.this);
                    }
                }
            });
            return true;
        }

        public boolean compareAndSetState(int expected, int state) {
            return STATE_UPDATER.compareAndSet(this, expected, state);
        }

        public int state() {
            return state;
        }

        @Override
        public boolean isCancelled() {
            return state() == ST_CANCELLED;
        }

        @Override
        public boolean isExpired() {
            return state() == ST_EXPIRED;
        }

        @Override
        public HashedWheelTimeout value() {
            return this;
        }

        public void expire() {
            if (!compareAndSetState(ST_INIT, ST_EXPIRED)) {
                return;
            }

            try {
                task.run(this);
            } catch (Throwable t) {
                log.warn("An exception was thrown by {}.", TimerTask.class.getSimpleName(), t);
            }
        }

        @Override
        public String toString() {
            final long currentTime = System.nanoTime();
            long remaining = deadline - currentTime + timer.startTime;

            StringBuilder buf = new StringBuilder(192)
                    .append(StringUtil.simpleClassName(this))
                    .append('(')
                    .append("deadline: ");
            if (remaining > 0) {
                buf.append(remaining)
                        .append(" ns later");
            } else if (remaining < 0) {
                buf.append(-remaining)
                        .append(" ns ago");
            } else {
                buf.append("now");
            }

            if (isCancelled()) {
                buf.append(", cancelled");
            }

            return buf.append(", task: ")
                    .append(task())
                    .append(')')
                    .toString();
        }
    }
}
/**
 * 简短回答：是的，每次任务执行（或取消）后，对应的 HashedWheelTimeout 实例都会从时间轮中移除并最终被 GC 回收。但 Netty 通过精心设计，使得这种“频繁创建/销毁”的开销在高并发场景下依然可控，甚至优于 JDK 的 ScheduledThreadPoolExecutor。
 * <p>
 * 下面我们深入分析为什么 “看似频繁创建/销毁” 实际上并不“很费”：
 * <p>
 * 🔍 1. 对象生命周期极短 + 结构极简
 * HashedWheelTimeout 是一个 轻量级 POJO：
 * 字段少（timer, task, deadline, state, next/prev, bucket 等）
 * 没有复杂逻辑
 * 不持有大资源（如线程、连接）
 * 在现代 JVM（尤其是 G1/ZGC）中，大量短生命周期的小对象分配和回收是非常高效的（分配在 Eden 区，Young GC 快速清理）。
 * 💡 对比：ScheduledThreadPoolExecutor 中的 ScheduledFutureTask 同样是一次性对象，也要被 GC。两者在这方面其实差不多，但 HashedWheelTimeout 更轻。
 * <p>
 * 🔍 2. 避免了优先队列的 O(log n) 开销
 * 这是 最关键的优势！
 * <p>
 * 操作
 * HashedWheelTimer
 * ScheduledThreadPoolExecutor
 * 插入任务
 * O(1)（计算槽位 + 链表头插）
 * O(log n)（堆插入）
 * 取消任务
 * O(1)（原子状态 + 延迟移除）
 * O(n)（需遍历队列找任务，实际是 O(n)）
 * 触发任务
 * O(k)（k = 当前槽任务数，通常很小）
 * O(log n)（poll 堆顶）
 * 当任务数 n = 100,000 时：
 * 时间轮：插入 ≈ 几条指令
 * 优先队列：插入 ≈ 17 次比较 + 可能的内存移动
 * ✅ 在海量任务场景下，O(1) vs O(log n) 的差距会被急剧放大，即使有对象创建开销，总体性能仍胜出。
 * <p>
 * 🔍 3. 批量处理 + 单线程无锁
 * 时间轮的 worker 线程每次 tick 只处理一个 bucket，该 bucket 中的任务是批量 expire 或 decrement。
 * 所有操作在单线程内完成，无需加锁（除了任务提交时的少量同步）。
 * 相比之下，ScheduledThreadPoolExecutor 的调度线程在高负载下可能成为瓶颈，且任务执行在线程池中，存在上下文切换开销。
 * 🔍 4. 内存局部性更好（Cache Friendly）
 * 时间轮的 bucket 是数组，每个 bucket 内的任务是链表。
 * worker 线程顺序访问 bucket 数组，CPU 缓存命中率高。
 * 而优先队列（二叉堆）在内存中是跳跃式访问，缓存局部性较差。
 * 🔍 5. Netty 的优化：对象池（可选）
 * 虽然默认没有启用，但 Netty 支持通过 PooledByteBufAllocator 等机制做对象池化。理论上，HashedWheelTimeout 也可以被池化（尽管目前未默认实现），进一步减少 GC。
 * <p>
 * 📊 实测数据参考（来自 Netty 官方和社区）
 * 在 10 万并发超时任务 场景下：
 * HashedWheelTimer：CPU 占用 < 5%，GC 几乎不可见
 * ScheduledThreadPoolExecutor：CPU 占用 > 20%，频繁 Young GC，偶尔 Full GC
 * 这就是为什么 Kafka、RocketMQ、Redis（部分模块）等系统都采用时间轮或类似结构处理超时。
 * <p>
 * ✅ 那么，“频繁创建/删除”真的不费吗？
 * 答案是：相对而言，它是最优解。
 * <p>
 * “费”是不可避免的：任何定时器都要管理任务生命周期。
 * 关键在于“如何费”：
 * 时间轮选择 “多创建轻量对象 + O(1) 操作”
 * 优先队列选择 “少创建对象 + O(log n) 操作”
 * 在 任务数量极大（>1万） 时，算法复杂度的差异远大于对象创建的开销。
 * 🔚 总结
 * 你的担忧
 * 实际情况
 * “频繁创建/删除 timeout 很费”
 * ✅ 对象轻量 + JVM 优化 → GC 开销可控
 * “不如复用对象？”
 * ⚠️ 复用会增加复杂度（状态重置、线程安全），得不偿失
 * “JDK 调度器不是也创建对象？”
 * ✅ 是的，但它的调度结构（堆）在大数据量下更“费”
 * 核心优势在哪？
 * O(1) 插入/取消 + 批量处理 + 低内存占用
 * 所以，HashedWheelTimer 的设计正是为了在“海量一次性任务”场景下，用“可控的对象创建开销”换取“极低的调度算法开销” —— 这是一个经过工业验证的优秀权衡。
 */
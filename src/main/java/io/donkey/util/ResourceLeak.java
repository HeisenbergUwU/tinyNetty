package io.donkey.util;

public interface ResourceLeak {
    // 记录当前调用栈
    void record();
    // 标记该资源已经正确释放
    boolean close();
}

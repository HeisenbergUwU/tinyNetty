package io.donkey.concurrent;

import java.util.Queue;

public abstract class AbstractScheduledEventExecutor extends AbstractEventExecutor {

    Queue<ScheduledFutureTask<?>> scheduledTaskQueue;
}

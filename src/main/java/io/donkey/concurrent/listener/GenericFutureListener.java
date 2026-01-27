package io.donkey.concurrent.listener;

import io.donkey.concurrent.future.Future;

import java.util.EventListener;

public interface GenericFutureListener<F extends Future<?>> extends EventListener {

    void operationComplete(F future) throws Exception;
}

package io.donkey.util.timer;



public interface TimerTask {

    void run(Timeout timeout) throws Exception;
}
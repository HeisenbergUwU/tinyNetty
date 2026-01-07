package io.donkey.common;

public interface ReferenceCounted {

    int refCnt();

    ReferenceCounted retain();

    ReferenceCounted retain(int increment);

    boolean release();
    
    boolean release(int decrement);
}

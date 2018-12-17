package com.bafomdad.realfilingcabinet.data;

/**
 * Created by bafomdad on 12/15/2018.
 */
public abstract class AbstractDataHolder<T> {

    public abstract T insert(int slot, T type);

    public abstract T extract(int slot, int amount);

    public abstract T getData(int slot);
}

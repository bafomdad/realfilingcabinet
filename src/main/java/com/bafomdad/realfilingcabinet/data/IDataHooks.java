package com.bafomdad.realfilingcabinet.data;

/**
 * Created by bafomdad on 12/15/2018.
 */

public interface IDataHooks {

    boolean hasDataHolder(EnumDataType type);

    <T extends AbstractDataHolder>T getDataHolder(EnumDataType type);
}

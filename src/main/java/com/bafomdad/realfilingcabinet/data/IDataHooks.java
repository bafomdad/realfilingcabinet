package com.bafomdad.realfilingcabinet.data;

import net.minecraft.util.math.Direction;

/**
 * Created by bafomdad on 12/15/2018.
 */

public interface IDataHooks {

    boolean hasDataHolder(EnumDataType type, Direction dir);

    <T extends AbstractDataHolder>T getDataHolder(EnumDataType type, Direction dir);
}

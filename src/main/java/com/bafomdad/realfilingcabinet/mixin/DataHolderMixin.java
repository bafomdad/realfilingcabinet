package com.bafomdad.realfilingcabinet.mixin;

import com.bafomdad.realfilingcabinet.data.AbstractDataHolder;
import com.bafomdad.realfilingcabinet.data.EnumDataType;
import com.bafomdad.realfilingcabinet.data.DataHooks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Created by bafomdad on 12/15/2018.
 */

@Mixin(BlockEntity.class)
public class DataHolderMixin implements DataHooks {

    @Override
    public boolean hasDataHolder(EnumDataType type, Direction dir) {

        return false;
    }

    @Override
    public AbstractDataHolder getDataHolder(EnumDataType type, Direction dir) {

        return null;
    }
}

package com.bafomdad.realfilingcabinet.mixin;

import com.bafomdad.realfilingcabinet.data.AbstractDataHolder;
import com.bafomdad.realfilingcabinet.data.EnumDataType;
import com.bafomdad.realfilingcabinet.data.IDataHooks;
import net.minecraft.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Created by bafomdad on 12/15/2018.
 */

@Mixin(BlockEntity.class)
public class DataHolderMixin implements IDataHooks {

    @Override
    public boolean hasDataHolder(EnumDataType type) {

        return false;
    }

    @Override
    public AbstractDataHolder getDataHolder(EnumDataType type) {

        return null;
    }
}

package com.bafomdad.realfilingcabinet.mixin;

import net.minecraft.client.render.block.BiomeColors;
import net.minecraft.client.render.block.GrassColorHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * Created by bafomdad on 12/15/2018.
 */
@Mixin(BiomeColors.class)
public class FuckYouBiomeColorsMixin {

    @Overwrite
    public static int grassColorAt(ExtendedBlockView world, BlockPos pos) {

        return GrassColorHandler.getColor(0.5D, 1.0D);
    }
}

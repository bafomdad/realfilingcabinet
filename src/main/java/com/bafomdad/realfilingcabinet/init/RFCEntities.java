package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.entity.FilingCabinetEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

/**
 * Created by bafomdad on 12/11/2018.
 */
public class RFCEntities {

    public static BlockEntityType<FilingCabinetEntity> FILINGCABINET_BE;

    public static void init() {

        FILINGCABINET_BE = registerBlockEntityType("filingcabinet", FilingCabinetEntity::new);
    }

    public static BlockEntityType registerBlockEntityType(String name, Supplier<BlockEntity> be) {

        return Registry.register(Registry.BLOCK_ENTITY, RealFilingCabinet.MODID + ":" + name, BlockEntityType.Builder.create(be).method_11034(null));
    }
}

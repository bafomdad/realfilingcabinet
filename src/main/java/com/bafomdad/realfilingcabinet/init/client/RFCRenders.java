package com.bafomdad.realfilingcabinet.init.client;

import com.bafomdad.realfilingcabinet.blocks.entity.FilingCabinetEntity;
import com.bafomdad.realfilingcabinet.render.RenderFilingCabinet;
import net.fabricmc.fabric.client.render.BlockEntityRendererRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;

/**
 * Created by bafomdad on 12/12/2018.
 */
public class RFCRenders {

    public static void init() {

        registerRender(FilingCabinetEntity.class, new RenderFilingCabinet());
    }

    private static void registerRender(Class<? extends BlockEntity> be, BlockEntityRenderer<? extends BlockEntity> ber) {

        BlockEntityRendererRegistry.INSTANCE.register(be, ber);
    }
}

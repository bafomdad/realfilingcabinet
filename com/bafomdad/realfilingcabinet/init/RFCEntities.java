package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.entity.ModelEntityCabinet;
import com.bafomdad.realfilingcabinet.entity.RenderEntityCabinet;
import com.bafomdad.realfilingcabinet.helpers.ResourceUpgradeHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RFCEntities {

	public static void init() {
		
		EntityRegistry.registerModEntity(new ResourceLocation(RealFilingCabinet.MOD_ID, "entitycabinet"), EntityCabinet.class, "cabinet", 0, RealFilingCabinet.instance, 64, 3, true, 0xFFFFFF, 0xEBE1DF);
	}
	
	@SideOnly(Side.CLIENT)
	public static void initModels() {
		
		RenderingRegistry.registerEntityRenderingHandler(EntityCabinet.class, RenderEntityCabinet.FACTORY);
	}
}

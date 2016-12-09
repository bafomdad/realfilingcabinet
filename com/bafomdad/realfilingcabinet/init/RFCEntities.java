package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.entity.ModelEntityCabinet;
import com.bafomdad.realfilingcabinet.entity.RenderEntityCabinet;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RFCEntities {

	public static void init() {
		
		EntityRegistry.registerModEntity(EntityCabinet.class, "cabinet", 0, RealFilingCabinet.instance, 64, 3, true);
		EntityRegistry.registerEgg(EntityCabinet.class, 0xFFFFFF, 0xEBE1DF);
	}
	
	@SideOnly(Side.CLIENT)
	public static void initModels() {
		
		RenderingRegistry.registerEntityRenderingHandler(EntityCabinet.class, RenderEntityCabinet.FACTORY);
	}
}

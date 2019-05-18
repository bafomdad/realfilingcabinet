package com.bafomdad.realfilingcabinet.init;

import java.util.Arrays;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.ISubModel;
import com.bafomdad.realfilingcabinet.blocks.tiles.*;
import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.entity.RenderEntityCabinet;
import com.bafomdad.realfilingcabinet.integration.BotaniaRFC;
import com.bafomdad.realfilingcabinet.integration.ThaumcraftRFC;
import com.bafomdad.realfilingcabinet.renders.*;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid=RealFilingCabinet.MOD_ID, value=Side.CLIENT)
public class RFCModelRegistryEvent {

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		
		RFCEventRegistry.blocks.forEach(b -> registerItemBlockModel(b));
		RFCEventRegistry.items.forEach(i -> registerItemModel(i));
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileFilingCabinet.class, new RenderFilingCabinet());
		RFCIntegration.canLoad(RFCIntegration.BOTANIA).ifPresent(t -> t.registerModels(event));
		RFCIntegration.canLoad(RFCIntegration.THAUMCRAFT).ifPresent(t -> t.registerModels(event));
		
		RenderingRegistry.registerEntityRenderingHandler(EntityCabinet.class, RenderEntityCabinet.FACTORY);
	}
	
	private static void registerItemBlockModel(Block block) {
		
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
	}
	
	private static void registerItemModel(Item item) {
		
		if (item instanceof ISubModel)
			((ISubModel)item).registerSubModels(item);
		else
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}

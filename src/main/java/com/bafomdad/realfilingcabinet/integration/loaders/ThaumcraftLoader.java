package com.bafomdad.realfilingcabinet.integration.loaders;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.BlockAspectCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileAspectCabinet;
import com.bafomdad.realfilingcabinet.crafting.FolderAspectRecipe;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.bafomdad.realfilingcabinet.init.RFCEventRegistry;
import com.bafomdad.realfilingcabinet.items.ItemAspectFolder;
import com.bafomdad.realfilingcabinet.items.itemblocks.ItemBlockAspect;
import com.bafomdad.realfilingcabinet.renders.RenderAspectCabinet;

public class ThaumcraftLoader {

	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		
		event.getRegistry().register(RFCEventRegistry.registerBlock(new BlockAspectCabinet(), "aspectcabinet").setHardness(2.0F).setResistance(1000.0F));
		GameRegistry.registerTileEntity(TileAspectCabinet.class, new ResourceLocation(RealFilingCabinet.MOD_ID, "aspectcabinet"));
	}
	
	public static void registerItems(RegistryEvent.Register<Item> event) {
		
		event.getRegistry().register(RFCEventRegistry.registerItem(new ItemAspectFolder(), "folder_aspect").setMaxStackSize(1));
		event.getRegistry().register(RFCEventRegistry.registerItem(new ItemBlockAspect(RFCBlocks.ASPECTCABINET), "aspectcabinet"));
	}
	
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		
		event.getRegistry().register(new FolderAspectRecipe().setRegistryName(new ResourceLocation(RealFilingCabinet.MOD_ID, "folderaspect")));
	}
	
	public static void registerModels(ModelRegistryEvent event) {
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileAspectCabinet.class, new RenderAspectCabinet());
	}
}

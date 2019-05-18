package com.bafomdad.realfilingcabinet.integration.loaders;

import vazkii.botania.api.BotaniaAPI;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.BlockManaCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileManaCabinet;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.bafomdad.realfilingcabinet.init.RFCEventRegistry;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemManaFolder;
import com.bafomdad.realfilingcabinet.items.itemblocks.ItemBlockMana;
import com.bafomdad.realfilingcabinet.renders.RenderManaCabinet;

public class BotaniaLoader {

	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		
		event.getRegistry().register(RFCEventRegistry.registerBlock(new BlockManaCabinet(), "manacabinet").setHardness(5.0F).setResistance(1000.0F));
		GameRegistry.registerTileEntity(TileManaCabinet.class, new ResourceLocation(RealFilingCabinet.MOD_ID, "manacabinet"));
	}
	
	public static void registerItems(RegistryEvent.Register<Item> event) {
		
		event.getRegistry().register(RFCEventRegistry.registerItem(new ItemManaFolder(), "folder_mana").setMaxStackSize(1));
		event.getRegistry().register(RFCEventRegistry.registerItem(new ItemBlockMana(RFCBlocks.MANACABINET), "manacabinet"));
	}
	
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		
		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(RFCItems.FOLDER_MANA), new ItemStack(RFCItems.EMPTYFOLDER, 1, 0), 2000);
		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(RFCBlocks.MANACABINET), new ItemStack(RFCBlocks.MODELCABINET), 9000);
	}
	
	public static void registerModels(ModelRegistryEvent event) {
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileManaCabinet.class, new RenderManaCabinet());
	}
}

package com.bafomdad.realfilingcabinet.init;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import vazkii.botania.api.BotaniaAPI;

import com.bafomdad.realfilingcabinet.NewConfigRFC.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.ItemBlockAC;
import com.bafomdad.realfilingcabinet.blocks.ItemBlockRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.crafting.*;
import com.bafomdad.realfilingcabinet.helpers.MobUpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.ResourceUpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.integration.BotaniaRFC;
import com.bafomdad.realfilingcabinet.items.ItemEmptyFolder;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.renders.RenderFilingCabinet;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RFCEventRegistry {

	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		
		RFCBlocks.init();
		if (RealFilingCabinet.botaniaLoaded && ConfigRFC.botaniaIntegration) {
			BotaniaRFC.initBlock();
			BotaniaRFC.initManaCabinet(event);
		}
		if (RealFilingCabinet.tcLoaded && ConfigRFC.tcIntegration)
			event.getRegistry().register(RFCBlocks.blockAC);
			
		event.getRegistry().register(RFCBlocks.blockRFC);
		GameRegistry.registerTileEntity(TileEntityRFC.class, RealFilingCabinet.MOD_ID  + "_tileFilingCabinet");
	}
	
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		
		RFCItems.init();
		if (RealFilingCabinet.botaniaLoaded && ConfigRFC.botaniaIntegration)
			BotaniaRFC.initManaFolder(event);
		if (RealFilingCabinet.tcLoaded && ConfigRFC.tcIntegration) {
			event.getRegistry().register(new ItemBlockAC(RFCBlocks.blockAC).setRegistryName(RFCBlocks.blockAC.getRegistryName()));
			event.getRegistry().register(RFCItems.aspectFolder);
		}
		event.getRegistry().register(new ItemBlockRFC(RFCBlocks.blockRFC).setRegistryName(RFCBlocks.blockRFC.getRegistryName()));
		
		event.getRegistry().register(RFCItems.debugger);
		event.getRegistry().register(RFCItems.emptyFolder);
		event.getRegistry().register(RFCItems.filter);
		event.getRegistry().register(RFCItems.folder);
		event.getRegistry().register(RFCItems.keys);
		event.getRegistry().register(RFCItems.magnifyingGlass);
		event.getRegistry().register(RFCItems.mysteryFolder);
		event.getRegistry().register(RFCItems.upgrades);
		event.getRegistry().register(RFCItems.whiteoutTape);
		event.getRegistry().register(RFCItems.suitcase);
		
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 0), StringLibs.TAG_CREATIVE);
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 1), StringLibs.TAG_CRAFT);
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 2), StringLibs.TAG_ENDER);
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 3), StringLibs.TAG_OREDICT);
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 4), StringLibs.TAG_MOB);
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 5), StringLibs.TAG_FLUID);
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 6), StringLibs.TAG_LIFE);
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 7), StringLibs.TAG_SMELT);
		
		MobUpgradeHelper.registerMobUpgrade(new ItemStack(RFCItems.upgrades, 1, 4), StringLibs.TAG_MOB);
		MobUpgradeHelper.registerMobUpgrade(new ItemStack(RFCItems.upgrades, 1, 5), StringLibs.TAG_FLUID);
	}
	
	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		
		if (RealFilingCabinet.botaniaLoaded && ConfigRFC.botaniaIntegration) {
			BotaniaAPI.registerManaInfusionRecipe(new ItemStack(BotaniaRFC.manaFolder), new ItemStack(RFCItems.emptyFolder, 1, 0), 2000);
			BotaniaAPI.registerManaInfusionRecipe(new ItemStack(BotaniaRFC.manaCabinet), new ItemStack(RFCBlocks.blockRFC), 9000);
		}
		if (RealFilingCabinet.tcLoaded && ConfigRFC.tcIntegration) {
			event.getRegistry().register(new FolderAspectRecipe("folderaspect"));
		}
		List<ItemStack> inputs1 = new ArrayList<ItemStack>() {{ add(new ItemStack(RFCItems.emptyFolder, 1, 0)); }};
		List<ItemStack> inputs2 = new ArrayList<ItemStack>() {{ add(new ItemStack(RFCItems.emptyFolder, 1, 1)); }};
		List<ItemStack> inputs3 = new ArrayList<ItemStack>() {{ add(new ItemStack(RFCItems.emptyFolder, 1, 4)); }};
		
		event.getRegistry().register(new FolderStorageRecipe("normalstoragefolder", new ItemStack(RFCItems.folder, 1, 0), inputs1));
		event.getRegistry().register(new FolderStorageRecipe("durabilitystoragefolder", new ItemStack(RFCItems.folder, 1, 2), inputs2));
		event.getRegistry().register(new FolderStorageRecipe("nbtstoragefolder", new ItemStack(RFCItems.folder, 1, 5), inputs3));
		event.getRegistry().register(new FolderExtractRecipe("folderextract"));
		event.getRegistry().register(new FolderMergeRecipe("foldermerge"));
		event.getRegistry().register(new FolderTapeRecipe("foldertape"));
	}
}

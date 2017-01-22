package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.items.*;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RFCItems {

	public static ItemEmptyFolder emptyFolder;
	public static ItemFolder folder;
	public static ItemMagnifyingGlass magnifyingGlass;
	public static ItemWhiteoutTape whiteoutTape;
	public static ItemUpgrades upgrades;
	public static ItemFilter filter;
	public static ItemKeys keys;
	public static ItemDebugger debugger;
	public static ItemMysteryFolder mysteryFolder;
	
	// Botania integration
	public static ItemManaFolder manaFolder;
	public static ItemManaUpgrade manaUpgrade;
	
	public static void init() {
		
		emptyFolder = new ItemEmptyFolder();
		folder = new ItemFolder();
		magnifyingGlass = new ItemMagnifyingGlass();
		whiteoutTape = new ItemWhiteoutTape();
		upgrades = new ItemUpgrades();
		filter = new ItemFilter();
		keys = new ItemKeys();
		debugger = new ItemDebugger();
		mysteryFolder = new ItemMysteryFolder();
		
		if (RealFilingCabinet.botaniaLoaded && ConfigRFC.botaniaIntegration) {
			manaFolder = new ItemManaFolder();
			manaUpgrade = new ItemManaUpgrade();
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void initModels() {
		
		for (int i = 0; i < emptyFolder.folderType.length; ++i)
			ModelLoader.setCustomModelResourceLocation(emptyFolder, i, new ModelResourceLocation(emptyFolder.getRegistryName() + "_" + emptyFolder.folderType[i], "inventory"));
		
		for (int i = 0; i < folder.folderTypes.length; ++i)
			ModelLoader.setCustomModelResourceLocation(folder, i, new ModelResourceLocation(folder.getRegistryName() + "_" + folder.folderTypes[i], "inventory"));
		
		for (int i = 0; i < upgrades.upgradeTypes.length; ++i)
			ModelLoader.setCustomModelResourceLocation(upgrades, i, new ModelResourceLocation(upgrades.getRegistryName() + "_" + upgrades.upgradeTypes[i], "inventory"));
		
		ModelLoader.setCustomModelResourceLocation(magnifyingGlass, 0, new ModelResourceLocation(magnifyingGlass.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(whiteoutTape, 0, new ModelResourceLocation(whiteoutTape.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(filter, 0, new ModelResourceLocation(filter.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(debugger, 0, new ModelResourceLocation(debugger.getRegistryName(), "inventory"));
		
		ModelLoader.setCustomModelResourceLocation(keys, 0, new ModelResourceLocation(keys.getRegistryName() + "_" + keys.keyTypes[0], "inventory"));
		ModelLoader.setCustomModelResourceLocation(keys, 1, new ModelResourceLocation(keys.getRegistryName() + "_" + keys.keyTypes[1], "inventory"));
		
		ModelLoader.setCustomModelResourceLocation(mysteryFolder, 0, new ModelResourceLocation(mysteryFolder.getRegistryName(), "inventory"));
		
		if (RealFilingCabinet.botaniaLoaded && ConfigRFC.botaniaIntegration) {
			ModelLoader.setCustomModelResourceLocation(manaFolder, 0, new ModelResourceLocation(manaFolder.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(manaUpgrade, 0, new ModelResourceLocation(manaUpgrade.getRegistryName(), "inventory"));
		}
	}
}

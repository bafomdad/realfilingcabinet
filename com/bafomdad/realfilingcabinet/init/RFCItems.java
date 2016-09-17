package com.bafomdad.realfilingcabinet.init;

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
	
	public static void init() {
		
		emptyFolder = new ItemEmptyFolder();
		folder = new ItemFolder();
		magnifyingGlass = new ItemMagnifyingGlass();
		whiteoutTape = new ItemWhiteoutTape();
		upgrades = new ItemUpgrades();
		filter = new ItemFilter();
		keys = new ItemKeys();
	}
	
	@SideOnly(Side.CLIENT)
	public static void initModels() {
		
		ModelLoader.setCustomModelResourceLocation(emptyFolder, 0, new ModelResourceLocation(emptyFolder.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(magnifyingGlass, 0, new ModelResourceLocation(magnifyingGlass.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(whiteoutTape, 0, new ModelResourceLocation(whiteoutTape.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(filter, 0, new ModelResourceLocation(filter.getRegistryName(), "inventory"));
		
		ModelLoader.setCustomModelResourceLocation(folder, 0, new ModelResourceLocation(folder.getRegistryName() + "_" + folder.folderTypes[0], "inventory"));
		ModelLoader.setCustomModelResourceLocation(folder, 1, new ModelResourceLocation(folder.getRegistryName() + "_" + folder.folderTypes[1], "inventory"));
		
		ModelLoader.setCustomModelResourceLocation(keys, 0, new ModelResourceLocation(keys.getRegistryName() + "_" + keys.keyTypes[0], "inventory"));
		ModelLoader.setCustomModelResourceLocation(keys, 1, new ModelResourceLocation(keys.getRegistryName() + "_" + keys.keyTypes[1], "inventory"));
		
		for (int i = 0; i < upgrades.upgradeTypes.length; ++i)
			ModelLoader.setCustomModelResourceLocation(upgrades, i, new ModelResourceLocation(upgrades.getRegistryName() + "_" + upgrades.upgradeTypes[i], "inventory"));
	}
}

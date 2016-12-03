package com.bafomdad.realfilingcabinet.integration;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.ResourceUpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.items.ItemManaFolder;
import com.bafomdad.realfilingcabinet.items.ItemManaUpgrade;

public class BotaniaRFC {
	
	private static final NavigableMap<Integer, String> manaSuffixes = new TreeMap();
	
	public static ItemManaFolder manaFolder;
	public static ItemManaUpgrade manaUpgrade;
	
	static {
		
		manaSuffixes.put(0, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana0"));
		manaSuffixes.put(120, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana120"));
		manaSuffixes.put(500, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana500"));
		manaSuffixes.put(2500, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana2500"));
		manaSuffixes.put(3000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana3000"));
		manaSuffixes.put(6000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana6000"));
		manaSuffixes.put(10000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana10000"));
		manaSuffixes.put(15000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana15000"));
		manaSuffixes.put(20000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana20000"));
		manaSuffixes.put(500000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana500000"));
		manaSuffixes.put(1000000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana1000000"));
		manaSuffixes.put(10000000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana10000000"));
		manaSuffixes.put(100000000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana10000000"));
		manaSuffixes.put(1000000000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana10000000"));
	}

	public static void register() {
		
		manaFolder = new ItemManaFolder();
		manaUpgrade = new ItemManaUpgrade();
	}
	
	public static void registerModels() {
		
		ModelLoader.setCustomModelResourceLocation(manaFolder, 0, new ModelResourceLocation(manaFolder.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(manaUpgrade, 0, new ModelResourceLocation(manaUpgrade.getRegistryName(), "inventory"));
		
		ResourceUpgradeHelper.registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/manacabinet.png"), StringLibs.TAG_MANA);
	}
	
	public static String formatMana(int value) {
		
		Entry<Integer, String> e = manaSuffixes.floorEntry(value);
		String suffix = e.getValue();
		
		return suffix;
	}
}

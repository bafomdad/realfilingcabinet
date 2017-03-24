package com.bafomdad.realfilingcabinet.integration;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.BlockManaCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileManaCabinet;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.items.ItemManaFolder;
import com.bafomdad.realfilingcabinet.renders.RenderManaCabinet;

public class BotaniaRFC {
	
	private static final NavigableMap<Integer, String> manaSuffixes = new TreeMap();
	
	public static Item manaFolder;
	public static Block manaCabinet;
	
	public static void initCommon() {
		
		manaFolder = new ItemManaFolder();
		manaCabinet = new BlockManaCabinet();
	}
	
	@SideOnly(Side.CLIENT)
	public static void initClient() {
		
		ModelLoader.setCustomModelResourceLocation(manaFolder, 0, new ModelResourceLocation(manaFolder.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(manaCabinet), 0, new ModelResourceLocation(manaCabinet.getRegistryName(), "inventory"));
		ClientRegistry.bindTileEntitySpecialRenderer(TileManaCabinet.class, new RenderManaCabinet());
		
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
	
	public static String formatMana(int value) {
		
		Entry<Integer, String> e = manaSuffixes.floorEntry(value);
		String suffix = e.getValue();
		
		return suffix;
	}
}

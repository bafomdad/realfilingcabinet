package com.bafomdad.realfilingcabinet.integration;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.BlockManaCabinet;
import com.bafomdad.realfilingcabinet.blocks.ItemBlockMana;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileManaCabinet;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.bafomdad.realfilingcabinet.items.ItemManaFolder;
import com.bafomdad.realfilingcabinet.renders.RenderManaCabinet;

public class BotaniaRFC {
	
	private static final NavigableMap<Integer, String> manaSuffixes = new TreeMap();
	
	public static Item manaFolder;
	public static Block manaCabinet;
	
	public static void initBlock() {
		
		manaCabinet = new BlockManaCabinet();
		GameRegistry.registerTileEntity(TileManaCabinet.class, "tileManaCabinet");
	}
	
	public static void initItem() {
		
		manaFolder = new ItemManaFolder();
	}
	
	public static void initManaFolder(RegistryEvent.Register<Item> event) {
		
		event.getRegistry().register(manaFolder);
		event.getRegistry().register(new ItemBlockMana(manaCabinet).setRegistryName(manaCabinet.getRegistryName()));
	}
	
	public static void initManaCabinet(RegistryEvent.Register<Block> event) {
		
		event.getRegistry().register(manaCabinet);
	}
	
	@SideOnly(Side.CLIENT)
	public static void initClient(ModelRegistryEvent event) {
		
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(manaCabinet), 0, new ModelResourceLocation(manaCabinet.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(manaFolder, 0, new ModelResourceLocation(manaFolder.getRegistryName(), "inventory"));
		
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

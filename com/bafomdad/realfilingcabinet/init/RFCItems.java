package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.NewConfigRFC.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.integration.BotaniaRFC;
import com.bafomdad.realfilingcabinet.items.*;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
	public static ItemSuitcase suitcase;
	
	// Thaumcraft integration
	public static ItemAspectFolder aspectFolder;
	
	public static void init() {
		
		if (RealFilingCabinet.botaniaLoaded && ConfigRFC.botaniaIntegration)
			BotaniaRFC.initItem();
		
		emptyFolder = new ItemEmptyFolder();
		folder = new ItemFolder();
		magnifyingGlass = new ItemMagnifyingGlass();
		whiteoutTape = new ItemWhiteoutTape();
		upgrades = new ItemUpgrades();
		filter = new ItemFilter();
		keys = new ItemKeys();
		debugger = new ItemDebugger();
		mysteryFolder = new ItemMysteryFolder();
		suitcase = new ItemSuitcase();
		
		if (RealFilingCabinet.tcLoaded && ConfigRFC.tcIntegration)
			aspectFolder = new ItemAspectFolder();
	}
}

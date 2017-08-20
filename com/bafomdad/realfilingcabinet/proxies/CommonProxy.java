package com.bafomdad.realfilingcabinet.proxies;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.MobUpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.init.*;
import com.bafomdad.realfilingcabinet.integration.BotaniaRFC;
import com.bafomdad.realfilingcabinet.items.ItemMysteryFolder;
import com.bafomdad.realfilingcabinet.network.RFCPacketHandler;
import com.bafomdad.realfilingcabinet.world.TutorialGenerator;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		
		RFCBlocks.init();
		RFCItems.init();
		RFCEntities.init();
		RFCPacketHandler.init();
		if (RealFilingCabinet.botaniaLoaded && ConfigRFC.botaniaIntegration)
			BotaniaRFC.initCommon();
	}
	
	public void init(FMLInitializationEvent event) {
		
		RFCRecipes.init();
		RFCIntegration.init();
		
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 0), StringLibs.TAG_CREATIVE);
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 1), StringLibs.TAG_CRAFT);
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 2), StringLibs.TAG_ENDER);
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 3), StringLibs.TAG_OREDICT);
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 4), StringLibs.TAG_MOB);
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 5), StringLibs.TAG_FLUID);
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 6), StringLibs.TAG_LIFE);
		
		MobUpgradeHelper.registerMobUpgrade(new ItemStack(RFCItems.upgrades, 1, 4), StringLibs.TAG_MOB);
		MobUpgradeHelper.registerMobUpgrade(new ItemStack(RFCItems.upgrades, 1, 5), StringLibs.TAG_FLUID);
		
		GameRegistry.registerWorldGenerator(new TutorialGenerator(), 1);
	}
	
	public void postInit(FMLPostInitializationEvent event) {}
	
	public void initAllModels() {}
}

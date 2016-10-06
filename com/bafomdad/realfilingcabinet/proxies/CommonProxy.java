package com.bafomdad.realfilingcabinet.proxies;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bafomdad.realfilingcabinet.api.RFCApi;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.*;
import com.bafomdad.realfilingcabinet.world.TutorialGenerator;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		
		RFCBlocks.init();
		RFCItems.init();
//		RFCPacketHandler.init();
	}
	
	public void init(FMLInitializationEvent event) {
		
		RFCRecipes.init();
		RFCIntegration.init();
		
		RFCApi.upgradeRegistry().registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 0), StringLibs.TAG_CREATIVE);
		RFCApi.upgradeRegistry().registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 1), StringLibs.TAG_CRAFT);
		RFCApi.upgradeRegistry().registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 2), StringLibs.TAG_ENDER);
		RFCApi.upgradeRegistry().registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 3), StringLibs.TAG_OREDICT);
		
		GameRegistry.registerWorldGenerator(new TutorialGenerator(), 1);
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
	public void initAllModels() {}
}

package com.bafomdad.realfilingcabinet.proxies;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bafomdad.realfilingcabinet.api.UpgradeHelper;
import com.bafomdad.realfilingcabinet.events.EventHandlerServer;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.*;
import com.bafomdad.realfilingcabinet.network.RFCPacketHandler;
import com.bafomdad.realfilingcabinet.renders.GuiFileList;
import com.bafomdad.realfilingcabinet.world.TutorialGenerator;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		
		RFCBlocks.init();
		RFCItems.init();
		RFCPacketHandler.init();
	}
	
	public void init(FMLInitializationEvent event) {
		
		RFCRecipes.init();
		RFCIntegration.init();
		
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 0), StringLibs.TAG_CREATIVE);
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 1), StringLibs.TAG_CRAFT);
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 2), StringLibs.TAG_ENDER);
		UpgradeHelper.registerUpgrade(new ItemStack(RFCItems.upgrades, 1, 3), StringLibs.TAG_OREDICT);
		
//		GameRegistry.registerWorldGenerator(new TutorialGenerator(), 1);
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
	public void initAllModels() {}
}

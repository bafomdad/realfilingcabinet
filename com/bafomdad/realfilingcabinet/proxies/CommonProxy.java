package com.bafomdad.realfilingcabinet.proxies;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
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
//import com.bafomdad.realfilingcabinet.integration.BotaniaRFC;
import com.bafomdad.realfilingcabinet.items.ItemMysteryFolder;
import com.bafomdad.realfilingcabinet.network.RFCPacketHandler;
import com.bafomdad.realfilingcabinet.world.TutorialGenerator;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		
		MinecraftForge.EVENT_BUS.register(new RFCEventRegistry());
		RFCEntities.init();
		RFCPacketHandler.init();
//		if (RealFilingCabinet.botaniaLoaded && ConfigRFC.botaniaIntegration)
//			BotaniaRFC.initCommon();
	}
	
	public void init(FMLInitializationEvent event) {
		
		RFCIntegration.init();
		GameRegistry.registerWorldGenerator(new TutorialGenerator(), 1);
	}
	
	public void postInit(FMLPostInitializationEvent event) {}
	
	public void initAllModels() {}
	
	public void registerColors() {}
}

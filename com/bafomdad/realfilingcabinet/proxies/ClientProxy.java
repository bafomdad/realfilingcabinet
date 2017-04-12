package com.bafomdad.realfilingcabinet.proxies;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.events.EventHandlerClient;
import com.bafomdad.realfilingcabinet.gui.GuiFileList;
import com.bafomdad.realfilingcabinet.helpers.ResourceUpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.bafomdad.realfilingcabinet.init.RFCEntities;
import com.bafomdad.realfilingcabinet.init.RFCIntegration;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.integration.BotaniaRFC;

public class ClientProxy extends CommonProxy {

	@Override
	public void initAllModels() {
		
		ResourceUpgradeHelper.registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/endercabinet.png"), StringLibs.TAG_ENDER);
		ResourceUpgradeHelper.registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/craftingcabinet.png"), StringLibs.TAG_CRAFT);
		ResourceUpgradeHelper.registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/oredictcabinet.png"), StringLibs.TAG_OREDICT);
		ResourceUpgradeHelper.registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/mobcabinet.png"), StringLibs.TAG_MOB);
		ResourceUpgradeHelper.registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/fluidcabinet.png"), StringLibs.TAG_FLUID);
		
//		ResourceUpgradeHelper.registerMobUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/entity/cabinetMobTexture.png"), StringLibs.TAG_MOB);
//		ResourceUpgradeHelper.registerMobUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/entity/cabinetFluidTexture.png"), StringLibs.TAG_FLUID);
		
		if (RealFilingCabinet.botaniaLoaded && ConfigRFC.botaniaIntegration)
			ResourceUpgradeHelper.registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/manacabinet.png"), StringLibs.TAG_MANA);
		
		RFCBlocks.initModels();
		RFCItems.initModels();
		RFCEntities.initModels();
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
		MinecraftForge.EVENT_BUS.register(new GuiFileList(Minecraft.getMinecraft()));
//		MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
	}
}

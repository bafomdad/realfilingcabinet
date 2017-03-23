package com.bafomdad.realfilingcabinet.proxies;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.ResourceUpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.bafomdad.realfilingcabinet.init.RFCEntities;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.integration.BotaniaRFC;
import com.bafomdad.realfilingcabinet.renders.GuiFileList;

public class ClientProxy extends CommonProxy {

	@Override
	public void initAllModels() {
		
		ResourceUpgradeHelper.registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/endercabinet.png"), StringLibs.TAG_ENDER);
		ResourceUpgradeHelper.registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/craftingcabinet.png"), StringLibs.TAG_CRAFT);
		ResourceUpgradeHelper.registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/oredictcabinet.png"), StringLibs.TAG_OREDICT);
		ResourceUpgradeHelper.registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/mobcabinet.png"), StringLibs.TAG_MOB);
		ResourceUpgradeHelper.registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/fluidcabinet.png"), StringLibs.TAG_FLUID);
		
		RFCBlocks.initModels();
		RFCItems.initModels();
		RFCEntities.initModels();
		if (RealFilingCabinet.botaniaLoaded && ConfigRFC.botaniaIntegration)
			BotaniaRFC.initClient();
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
		MinecraftForge.EVENT_BUS.register(new GuiFileList(Minecraft.getMinecraft()));
	}
}

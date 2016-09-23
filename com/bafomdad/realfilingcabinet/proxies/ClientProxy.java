package com.bafomdad.realfilingcabinet.proxies;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.RFCApi;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.*;
import com.bafomdad.realfilingcabinet.renders.GuiFileList;

public class ClientProxy extends CommonProxy {

	@Override
	public void initAllModels() {
		
		RFCApi.resourceRegistry().registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/endercabinet.png"), StringLibs.TAG_ENDER);
		RFCApi.resourceRegistry().registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/craftingcabinet.png"), StringLibs.TAG_CRAFT);
		RFCApi.resourceRegistry().registerUpgradeResource(new ResourceLocation(RealFilingCabinet.MOD_ID, "textures/model/oredictcabinet.png"), StringLibs.TAG_OREDICT);
		
		RFCBlocks.initModels();
		RFCItems.initModels();
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
		MinecraftForge.EVENT_BUS.register(new GuiFileList(Minecraft.getMinecraft()));
	}
}

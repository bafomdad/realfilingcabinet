package com.bafomdad.realfilingcabinet.proxies;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.init.*;
import com.bafomdad.realfilingcabinet.renders.GuiFileList;

public class ClientProxy extends CommonProxy {

	@Override
	public void initAllModels() {
		
		RFCBlocks.initModels();
		RFCItems.initModels();
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
		MinecraftForge.EVENT_BUS.register(new GuiFileList(Minecraft.getMinecraft()));
	}
}

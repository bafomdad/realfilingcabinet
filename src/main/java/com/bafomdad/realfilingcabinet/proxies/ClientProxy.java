package com.bafomdad.realfilingcabinet.proxies;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.bafomdad.realfilingcabinet.gui.GuiFileList;
import com.bafomdad.realfilingcabinet.network.RFCPacketHandler;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		
		super.preInit(event);
		RFCPacketHandler.initClient();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
		MinecraftForge.EVENT_BUS.register(new GuiFileList());
	}
}

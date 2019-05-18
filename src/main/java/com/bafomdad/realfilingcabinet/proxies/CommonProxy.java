package com.bafomdad.realfilingcabinet.proxies;

import com.bafomdad.realfilingcabinet.network.RFCPacketHandler;

import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		
		RFCPacketHandler.init();
	}

	public void postInit(FMLPostInitializationEvent event) {}
}

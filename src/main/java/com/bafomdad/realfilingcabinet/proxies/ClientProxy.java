package com.bafomdad.realfilingcabinet.proxies;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import com.bafomdad.realfilingcabinet.gui.GuiFileList;

public class ClientProxy extends CommonProxy {

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
		MinecraftForge.EVENT_BUS.register(new GuiFileList());
	}
}

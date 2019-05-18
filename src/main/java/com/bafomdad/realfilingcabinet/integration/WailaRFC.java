package com.bafomdad.realfilingcabinet.integration;

import net.minecraftforge.fml.common.event.FMLInterModComms;

public class WailaRFC implements IModCompat {

	@Override
	public String getModID() {

		return "waila";
	}

	@Override
	public boolean isConfigEnabled() {

		return true;
	}
	
	@Override
	public void register() {
		
		FMLInterModComms.sendMessage(getModID(), "register", "com.bafomdad.realfilingcabinet.integration.loaders.WailaLoader.load");
	}
}

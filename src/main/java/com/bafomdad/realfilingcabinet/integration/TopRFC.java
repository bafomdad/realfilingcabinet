package com.bafomdad.realfilingcabinet.integration;

import net.minecraftforge.fml.common.event.FMLInterModComms;

public class TopRFC implements IModCompat {

	@Override
	public String getModID() {

		return "theoneprobe";
	}

	@Override
	public boolean isConfigEnabled() {

		return true;
	}

	@Override
	public void register() {
		
		FMLInterModComms.sendFunctionMessage(getModID(), "getTheOneProbe", "com.bafomdad.realfilingcabinet.integration.loaders.ProbeLoader$GetTheOneProbe");
	}
}

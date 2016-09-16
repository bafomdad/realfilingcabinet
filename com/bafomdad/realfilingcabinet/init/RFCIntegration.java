package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.integration.WailaRFC;

import net.minecraftforge.fml.common.Loader;

public class RFCIntegration {

	public static void init() {
		
		if (Loader.isModLoaded("Waila"))
			WailaRFC.register();
	}
}

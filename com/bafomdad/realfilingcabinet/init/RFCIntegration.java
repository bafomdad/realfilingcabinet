package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.integration.*;

import net.minecraftforge.fml.common.Loader;

public class RFCIntegration {

	public static void init() {
		
		if (Loader.isModLoaded("Waila"))
			WailaRFC.register();
		if (Loader.isModLoaded("theoneprobe"))
			TopRFC.register();
	}
}

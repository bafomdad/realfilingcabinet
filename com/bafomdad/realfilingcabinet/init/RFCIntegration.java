package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.integration.*;

import net.minecraftforge.fml.common.Loader;

public class RFCIntegration {

	public static void init() {
		
		if (RealFilingCabinet.topLoaded)
			TopRFC.register();
		if (RealFilingCabinet.wailaLoaded)
			WailaRFC.register();
		EnderIORFC.register();
	}
}

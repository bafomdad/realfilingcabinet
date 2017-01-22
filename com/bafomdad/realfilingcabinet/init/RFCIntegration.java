package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.integration.*;

public class RFCIntegration {

	public static void init() {
		
		if (RealFilingCabinet.wailaLoaded)
			WailaRFC.register();
		if (RealFilingCabinet.topLoaded)
			TopRFC.register();
		if (RealFilingCabinet.enderioLoaded)
			EnderIORFC.register();
	}
}

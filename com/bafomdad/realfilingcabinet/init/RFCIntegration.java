package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.integration.*;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public class RFCIntegration {
	
	public static void preInit() {
		
		if (RealFilingCabinet.botaniaLoaded && ConfigRFC.botaniaIntegration)
			BotaniaRFC.register();
	}

	public static void init() {
		
		if (RealFilingCabinet.wailaLoaded)
			WailaRFC.register();
		if (RealFilingCabinet.topLoaded)
			TopRFC.register();
		if (RealFilingCabinet.botaniaLoaded && ConfigRFC.botaniaIntegration)
			UpgradeHelper.registerUpgrade(new ItemStack(BotaniaRFC.manaUpgrade, 1, 0), StringLibs.TAG_MANA);
	}
}

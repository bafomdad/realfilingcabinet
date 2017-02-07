package com.bafomdad.realfilingcabinet.integration;

import net.minecraftforge.fml.common.event.FMLInterModComms;

import com.bafomdad.realfilingcabinet.LogRFC;

public class EnderIORFC {

	public static void register() {
		
		LogRFC.debug("Sending IMC message to EnderIO...");
		FMLInterModComms.sendMessage("enderio", "soulVial:blacklist:add", "realfilingcabinet.cabinet");
	}
}

package com.bafomdad.realfilingcabinet.integration;

import com.bafomdad.realfilingcabinet.LogRFC;

import net.minecraftforge.fml.common.event.FMLInterModComms;

public class EnderIORFC {

	public static void register() {
		
		LogRFC.info("Sending IMC message to EnderIO...");
		FMLInterModComms.sendMessage("EnderIO", "soulVial:blacklist:add", "realfilingcabinet.cabinet");
	}
}

package com.bafomdad.realfilingcabinet.integration;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;

public class BotaniaRFC {
	
	private static final NavigableMap<Integer, String> manaSuffixes = new TreeMap();
	
	static {
		
		manaSuffixes.put(0, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana0"));
		manaSuffixes.put(120, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana120"));
		manaSuffixes.put(500, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana500"));
		manaSuffixes.put(2500, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana2500"));
		manaSuffixes.put(3000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana3000"));
		manaSuffixes.put(6000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana6000"));
		manaSuffixes.put(10000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana10000"));
		manaSuffixes.put(15000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana15000"));
		manaSuffixes.put(20000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana20000"));
		manaSuffixes.put(500000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana500000"));
		manaSuffixes.put(1000000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana1000000"));
		manaSuffixes.put(10000000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana10000000"));
		manaSuffixes.put(100000000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana10000000"));
		manaSuffixes.put(1000000000, TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".mana10000000"));
	}
	
	public static String formatMana(int value) {
		
		Entry<Integer, String> e = manaSuffixes.floorEntry(value);
		String suffix = e.getValue();
		
		return suffix;
	}
}

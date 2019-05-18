package com.bafomdad.realfilingcabinet.helpers;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

public class TextHelper {

	private static final NavigableMap<Long, String> SUFFIXES = new TreeMap();
	private static final NavigableMap<Integer, String> MANASUFFIXES = new TreeMap();
	
	static {
		SUFFIXES.put(1000L, "k");
		SUFFIXES.put(1000000L, "M");
		SUFFIXES.put(1000000000L, "b");
		SUFFIXES.put(1000000000000L, "T");
		SUFFIXES.put(1000000000000000L, "q");
		
		MANASUFFIXES.put(0, StringLibs.TOOLTIP + ".mana0");
		MANASUFFIXES.put(120, StringLibs.TOOLTIP + ".mana120");
		MANASUFFIXES.put(500, StringLibs.TOOLTIP + ".mana500");
		MANASUFFIXES.put(2500, StringLibs.TOOLTIP + ".mana2500");
		MANASUFFIXES.put(3000, StringLibs.TOOLTIP + ".mana3000");
		MANASUFFIXES.put(6000, StringLibs.TOOLTIP + ".mana6000");
		MANASUFFIXES.put(10000, StringLibs.TOOLTIP + ".mana10000");
		MANASUFFIXES.put(15000, StringLibs.TOOLTIP + ".mana15000");
		MANASUFFIXES.put(20000, StringLibs.TOOLTIP + ".mana20000");
		MANASUFFIXES.put(500000, StringLibs.TOOLTIP + ".mana500000");
		MANASUFFIXES.put(1000000, StringLibs.TOOLTIP + ".mana1000000");
		MANASUFFIXES.put(10000000, StringLibs.TOOLTIP + ".mana10000000");
		MANASUFFIXES.put(100000000, StringLibs.TOOLTIP + ".mana10000000");
		MANASUFFIXES.put(1000000000, StringLibs.TOOLTIP + ".mana10000000");
	}
	
	public static String format(long value) {
		
		if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
		if (value < 0) return "-" + format(-value);
		if (value < 1000) return Long.toString(value);
		
		Entry<Long, String> e = SUFFIXES.floorEntry(value);
		Long divideBy = e.getKey();
		String suffix = e.getValue();
		
		long truncated = value / (divideBy / 10);
		boolean hasDecimal = truncated < 100 && (truncated / 10.0D) != (truncated / 10);
		
		return hasDecimal ? (truncated / 10D) + suffix : (truncated / 10) + suffix;
	}
	
	public static String formatMana(int value) {
		
		Entry<Integer, String> e = MANASUFFIXES.floorEntry(value);
		String suffix = e.getValue();
		
		return suffix;
	}
}

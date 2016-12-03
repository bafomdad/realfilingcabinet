package com.bafomdad.realfilingcabinet.helpers;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import net.minecraft.util.text.translation.I18n;

public class TextHelper {

	private static final NavigableMap<Long, String> suffixes = new TreeMap();
	
	static {
		suffixes.put(1000L, "k");
		suffixes.put(1000000L, "M");
		suffixes.put(1000000000L, "b");
		suffixes.put(1000000000000L, "T");
		suffixes.put(1000000000000000L, "q");
	}
	
	public static String format(long value) {
		
		if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
		if (value < 0) return "-" + format(-value);
		if (value < 1000) return Long.toString(value);
		
		Entry<Long, String> e = suffixes.floorEntry(value);
		Long divideBy = e.getKey();
		String suffix = e.getValue();
		
		long truncated = value / (divideBy / 10);
		boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
		
		return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
	}
	
	public static String localize(String str) {
		
		return I18n.translateToLocal(str);
	}
}

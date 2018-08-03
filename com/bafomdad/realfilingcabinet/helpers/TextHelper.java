package com.bafomdad.realfilingcabinet.helpers;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.items.ItemEmptyFolder.FolderType;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

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
	
	// client only
	public static String localize(String str) {
		
		return I18n.format(str);
	}
	
	// server only
	public static String localizeCommands(String str) {
		
		return net.minecraft.util.text.translation.I18n.translateToLocalFormatted("commands." + RealFilingCabinet.MOD_ID + "." + str);
	}
	
	public static String folderStr(ItemStack folder) {
		
		if (!(folder.getItem() instanceof IFolder))
			return null;
		
		if (ItemFolder.getObject(folder) != null) {
			if (ItemFolder.getObject(folder) instanceof ItemStack)
				return ((ItemStack)ItemFolder.getObject(folder)).getDisplayName();
			if (ItemFolder.getObject(folder) instanceof FluidStack)
				return ((FluidStack)ItemFolder.getObject(folder)).getLocalizedName();
			else if (ItemFolder.getObject(folder) instanceof String) {
				return (String)ItemFolder.getObject(folder);
			}
		}
		return null;
	}
}

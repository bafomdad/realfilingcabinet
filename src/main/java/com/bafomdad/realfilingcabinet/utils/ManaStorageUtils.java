package com.bafomdad.realfilingcabinet.utils;

import com.bafomdad.realfilingcabinet.helpers.StringLibs;

import net.minecraft.item.ItemStack;

public class ManaStorageUtils {

	public static final int maxCount = 1000000000;
	
	public static void setManaSize(ItemStack stack, int count) {
		
		NBTUtils.setInt(stack, StringLibs.TAG_MANA_COUNT, Math.max(0, count));
	}
	
	public static void addManaToFolder(ItemStack stack, int count) {
		
		int current = getManaSize(stack);
		setManaSize(stack, current + count);
	}
	
	public static int getManaSize(ItemStack stack) {
		
		return NBTUtils.getInt(stack, StringLibs.TAG_MANA_COUNT, 0);
	}
	
	public static boolean isManaFolderFull(ItemStack stack) {
		
		return getManaSize(stack) >= maxCount;
	}
	
	public static int getMaxManaFolder() {
		
		return maxCount;
	}
}

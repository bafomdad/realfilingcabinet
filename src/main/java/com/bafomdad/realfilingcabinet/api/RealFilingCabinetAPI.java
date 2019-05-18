package com.bafomdad.realfilingcabinet.api;

import java.util.LinkedHashSet;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.bafomdad.realfilingcabinet.api.upgrades.*;

public class RealFilingCabinetAPI {

	public static final LinkedHashSet<Upgrades> UPGRADES = new LinkedHashSet();
	public static final LinkedHashSet<MobUpgrades> MOBUPGRADES = new LinkedHashSet();
	
	/**
	 * Put this in init phase or sometime after you've registered all your items. Item must implement IUpgrade, and tag must not be empty. 
	 * Returning null on ResourceLocation will make the filing cabinet fallback to default texture
	 * @param stack
	 * @param texture
	 * @param tag
	 */
	public static void registerUpgrade(ItemStack stack, ResourceLocation texture, String tag) {
		
		if (stack.isEmpty() || !(stack.getItem() instanceof IUpgrade))
			throw new IllegalArgumentException("[RealFilingCabinet]: Itemstack upgrade cannot be empty, or the item itself must implement IUpgrade");
		if (tag == null || tag.isEmpty())
			throw new IllegalArgumentException("[RealFilingCabinet]: Upgrade tag string cannot be null or empty.");
		
		UPGRADES.add(new Upgrades(stack, texture, tag));
	}
	
	/**
	 * Entity version of upgrades
	 * Register it the same as you would as the registration method above
	 * @param stack
	 * @param Fully qualified string to load the ModelBase class instance with
	 * @param texture
	 * @param tag
	 */
	public static void registerMobUpgrade(ItemStack stack, String modelClass, ResourceLocation texture, String tag) {
		
		if (stack.isEmpty() || !(stack.getItem() instanceof IUpgrade))
			throw new IllegalArgumentException("[RealFilingCabinet]: Itemstack upgrade cannot be empty, or the item itself must implement IUpgrade");
		if (tag == null || tag.isEmpty())
			throw new IllegalArgumentException("[RealFilingCabinet]: Upgrade tag string cannot be null or empty.");

		MOBUPGRADES.add(new MobUpgrades(stack, modelClass, texture, tag));
	}
}

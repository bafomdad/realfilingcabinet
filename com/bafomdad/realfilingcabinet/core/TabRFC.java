package com.bafomdad.realfilingcabinet.core;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TabRFC extends CreativeTabs {

	public static TabRFC instance = new TabRFC();
	
	public TabRFC() {
		
		super(RealFilingCabinet.MOD_ID + ".tabRFC.name");
	}
	
	public ItemStack getIconItemStack() {
		
		return new ItemStack(RealFilingCabinet.itemFolder);
	}
	
	public Item getTabIconItem() {
		
		return new Item();
	}
}

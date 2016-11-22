package com.bafomdad.realfilingcabinet;

import com.bafomdad.realfilingcabinet.init.RFCItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TabRFC extends CreativeTabs {

	public static TabRFC instance = new TabRFC();
	
	public TabRFC() {
		
		super(RealFilingCabinet.MOD_ID + ".tabRFC.name");
	}
	
	@Override
	public ItemStack getIconItemStack() {
		
		return new ItemStack(RFCItems.emptyFolder);
	}

	@Override
	public ItemStack getTabIconItem() {

		return new ItemStack(RFCItems.emptyFolder);
	}
}

package com.bafomdad.realfilingcabinet;

import com.bafomdad.realfilingcabinet.init.RFCItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class TabRFC extends CreativeTabs {

	public static TabRFC instance = new TabRFC();
	
	public TabRFC() {
		
		super(RealFilingCabinet.MOD_ID + ".tabRFC.name");
	}

	@Override
	public ItemStack createIcon() {

		return new ItemStack(RFCItems.EMPTYFOLDER);
	}
}

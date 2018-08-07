package com.bafomdad.realfilingcabinet;

import com.bafomdad.realfilingcabinet.init.RFCItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TabRFC extends CreativeTabs {

	public static TabRFC instance = new TabRFC();
	
	private ItemStack icon;
	
	public TabRFC() {
		
		super(RealFilingCabinet.MOD_ID + ".tabRFC.name");
	}
	
	@Override
	public ItemStack createIcon() {
		if(icon == null)
		{
			icon = new ItemStack(RFCItems.emptyFolder);
		}
		
		return icon;
	}

	@Override
	public ItemStack getIcon() {
		if(icon == null)
		{
			icon = new ItemStack(RFCItems.emptyFolder);
		}
		
		return icon;
	}
}

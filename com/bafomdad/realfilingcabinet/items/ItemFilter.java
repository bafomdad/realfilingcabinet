package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;

public class ItemFilter extends Item {
	
	private static String TAG_FILTER = "RFC_tagFilter";

	public ItemFilter() {
		
		setRegistryName("filter");
		setUnlocalizedName(RealFilingCabinet.MOD_ID + ".filter");
		setMaxStackSize(16);
		setCreativeTab(TabRFC.instance);
		GameRegistry.register(this);
	}
	
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean whatisthis) {
		
		list.add("This goes in a item frame");
	}
}

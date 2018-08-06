package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemFilter extends Item {

	public ItemFilter() {
		
		setRegistryName("filter");
		setTranslationKey(RealFilingCabinet.MOD_ID + ".filter");
		setMaxStackSize(16);
		setCreativeTab(TabRFC.instance);
	}
	
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean whatisthis) {
		
		list.add(TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".itemfilter"));
	}
}

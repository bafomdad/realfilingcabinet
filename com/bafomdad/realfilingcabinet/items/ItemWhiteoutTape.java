package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemWhiteoutTape extends Item {

	public ItemWhiteoutTape() {
		
		setRegistryName("whiteouttape");
		setUnlocalizedName(RealFilingCabinet.MOD_ID + ".whiteouttape");
		setMaxStackSize(1);
		setMaxDamage(25);
		setCreativeTab(TabRFC.instance);
		GameRegistry.register(this);
	}
	
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean whatisthis) {
		
		list.add("Craft with any folder with 0 items or blocks");
	}
	
	public ItemStack getContainerItem(ItemStack stack) {
		
		ItemStack copy = stack.copy();
		copy.setItemDamage(copy.getItemDamage() + 1);
		
		return copy;
	}
	
	public boolean hasContainerItem(ItemStack stack) {
		
		return getContainerItem(stack) != null && stack.getItem() == this;
	}
}

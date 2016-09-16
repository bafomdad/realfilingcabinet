package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemEmptyFolder extends Item {
	
	public ItemEmptyFolder() {
		
		setRegistryName("emptyfolder");
		setUnlocalizedName(RealFilingCabinet.MOD_ID + ".emptyfolder");
		setMaxStackSize(8);
		setCreativeTab(TabRFC.instance);
		GameRegistry.register(this);
	}
	
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean whatisthis) {
		
		list.add("<Empty>");
	}
}

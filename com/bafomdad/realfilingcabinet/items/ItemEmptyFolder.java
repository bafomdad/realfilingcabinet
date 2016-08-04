package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemEmptyFolder extends Item {
	
	public ItemEmptyFolder() {
		
		setMaxStackSize(8);
	}

    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean whatisthis) {
    	
    	list.add("<Empty>");
    }
}

package com.bafomdad.realfilingcabinet.utils;

import net.minecraft.item.ItemStack;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

public class DurabilityUtils {

	public static boolean matchDurability(TileEntityRFC tile, ItemStack stack) {
		
		if (stack == null)
			return false;
		
		for (int i = 0; i < tile.getInventory().getSlots(); i++) {
			ItemStack folder = tile.getInventory().getTrueStackInSlot(i);
			if (folder != null && folder.getItem() == RFCItems.folder) {
				if (folder.getItemDamage() == 2 && ItemFolder.getObject(folder) != null)
				{
					if (stack.getItem() == ((ItemStack)ItemFolder.getObject(folder)).getItem()) {
						if (stack.hasTagCompound() && !NBTUtils.getBoolean(folder, StringLibs.RFC_IGNORENBT, false))
							return false;
						
						int remSize = stack.getItemDamage();
						int storedRem = ItemFolder.getRemSize(folder);
						
						ItemFolder.addRem(folder, stack.getMaxDamage() - stack.getItemDamage());
						int newRem = ItemFolder.getRemSize(folder);
						
						if (newRem >= stack.getMaxDamage())
						{
							ItemFolder.add(folder, 1);
							int newStoredRem = newRem - stack.getMaxDamage();
							ItemFolder.setRemSize(folder, newStoredRem);
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean matchDurability(TileEntityRFC tile, ItemStack stack, int slot, boolean simulate) {
		
		if (stack.isEmpty())
			return false;
		
		ItemStack folder = tile.getInventory().getTrueStackInSlot(slot);
		if (!folder.isEmpty() && folder.getItem() == RFCItems.folder) {
			if (folder.getItemDamage() == 2 && ItemFolder.getObject(folder) != null) {
				if (stack.getItem() == ((ItemStack)ItemFolder.getObject(folder)).getItem()) {
					if (stack.hasTagCompound() && !NBTUtils.getBoolean(folder, StringLibs.RFC_IGNORENBT, false))
						return false;
					
					if (!simulate) {
						int remSize = stack.getItemDamage();
						int storedRem = ItemFolder.getRemSize(folder);
						
						if (remSize == 0)
							ItemFolder.add(folder, 1);
						
						ItemFolder.addRem(folder, stack.getMaxDamage() - stack.getItemDamage());
						int newRem = ItemFolder.getRemSize(folder);
						
						if (newRem >= stack.getMaxDamage()) {
							ItemFolder.add(folder, 1);
							int newStoredRem = newRem - stack.getMaxDamage();
							ItemFolder.setRemSize(folder, newStoredRem);
						}
					}
					return true;
				}
			}
		}
		return false;
	}
}

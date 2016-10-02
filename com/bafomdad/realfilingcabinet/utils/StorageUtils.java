package com.bafomdad.realfilingcabinet.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import com.bafomdad.realfilingcabinet.api.helper.UpgradeHelper;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

public class StorageUtils {
	
	public static int simpleFolderMatch(TileEntityRFC tile, ItemStack stack) {
		
		if (stack == null)
			return -1;
		
		for (int i = 0; i < tile.getInventory().getSlots(); i++) {
			ItemStack loopinv = tile.getInventory().getStackFromFolder(i);
			if (UpgradeHelper.getUpgrade(tile, StringLibs.TAG_OREDICT) != null)
			{
				OreDictUtils.recreateOreDictionary(stack);
				if (OreDictUtils.hasOreDict())
				{
					if (loopinv != null && OreDictUtils.areItemsEqual(stack, loopinv)) {
						return i;
					}
				}
			}
			if (loopinv != null && simpleMatch(stack, loopinv))
				return i;
		}
		return -1;
	}

	public static void addStackManually(TileEntityRFC tile, EntityPlayer player, ItemStack stack) {
		
		if (tile.getWorld().isRemote)
			return;
		
		for (int i = 0; i < tile.getInventory().getSlots(); i++) {
			ItemStack loopinv = tile.getInventory().getStackFromFolder(i);
			if (UpgradeHelper.getUpgrade(tile, StringLibs.TAG_OREDICT) != null)
			{
				OreDictUtils.recreateOreDictionary(stack);
				if (OreDictUtils.hasOreDict()) {
					if (loopinv != null && OreDictUtils.areItemsEqual(stack, loopinv))
					{
						ItemFolder.add(tile.getInventory().getTrueStackInSlot(i), stack.stackSize);
						player.setHeldItem(EnumHand.MAIN_HAND, null);
						tile.markBlockForUpdate();
						break;
					}
				}
			}
			if (loopinv != null && simpleMatch(loopinv, stack))
			{
				ItemFolder.add(tile.getInventory().getTrueStackInSlot(i), stack.stackSize);
				player.setHeldItem(EnumHand.MAIN_HAND, null);
				tile.markBlockForUpdate();
				break;
			}
		}
	}
	
	public static void addAllStacksManually(TileEntityRFC tile, EntityPlayer player) {
		
		if (tile.getWorld().isRemote)
			return;
		
		boolean consume = false;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack loopinv = player.inventory.getStackInSlot(i);
			if (loopinv != null && (loopinv.getItem() != RFCItems.emptyFolder || loopinv.getItem() != RFCItems.folder))
			{
				for (int j = 0; j < tile.getInventory().getSlots(); j++) {
					ItemStack folderstack = tile.getInventory().getStackFromFolder(j);
					if (ItemStack.areItemsEqual(folderstack, loopinv))
					{
						ItemFolder.add(tile.getInventory().getTrueStackInSlot(j), loopinv.stackSize);
						player.inventory.setInventorySlotContents(i, null);
						consume = true;
						break;
					}
				}
			}
		}
		if (consume)
		{
			updatePlayerInventory(player);
			tile.markDirty();
		}
	}
	
	public static void extractStackManually(TileEntityRFC tile, EntityPlayer player, boolean crouching) {
		
		ItemStack stack = tile.getFilter();
		if (stack != null) {
			for (int i = 0; i < tile.getInventory().getSlots(); i++) {
				ItemStack loopinv = tile.getInventory().getStackFromFolder(i);
				if (UpgradeHelper.getUpgrade(tile, StringLibs.TAG_OREDICT) != null)
				{
					OreDictUtils.recreateOreDictionary(stack);
					if (OreDictUtils.hasOreDict()) {
						if (loopinv != null && OreDictUtils.areItemsEqual(stack, loopinv))
						{
							ItemStack folder = tile.getInventory().getTrueStackInSlot(i);
							long count = ItemFolder.getFileSize(folder);
							if (crouching) {
								long extract = Math.min(stack.getMaxStackSize(), count);
								ItemStack stackExtract = new ItemStack(stack.getItem(), (int)extract, stack.getItemDamage());
								player.inventory.addItemStackToInventory(stackExtract);
								ItemFolder.remove(folder, extract);
								tile.markBlockForUpdate();
								break;
							}
							else
							{
								ItemStack stackExtract = new ItemStack(stack.getItem(), 1, stack.getItemDamage());
								player.inventory.addItemStackToInventory(stackExtract);
								ItemFolder.remove(folder, 1);
								tile.markBlockForUpdate();
								break;
							}
						}
					}
				}
				if (loopinv != null && simpleMatch(loopinv, stack))
				{
					ItemStack folder = tile.getInventory().getTrueStackInSlot(i);
					long count = ItemFolder.getFileSize(folder);
					if (crouching) {
						long extract = Math.min(stack.getMaxStackSize(), count);
						ItemStack stackExtract = new ItemStack(stack.getItem(), (int)extract, stack.getItemDamage());
						player.inventory.addItemStackToInventory(stackExtract);
						ItemFolder.remove(folder, extract);
						tile.markBlockForUpdate();
						break;
					}
					else
					{
						ItemStack stackExtract = new ItemStack(stack.getItem(), 1, stack.getItemDamage());
						player.inventory.addItemStackToInventory(stackExtract);
						ItemFolder.remove(folder, 1);
						tile.markBlockForUpdate();
						break;
					}
				}
			}
		}
	}
	
	public static boolean simpleMatch(ItemStack stack1, ItemStack stack2) {
		
		return stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage();
	}
	
	public static void updatePlayerInventory(EntityPlayer player) {
		
		if (player instanceof EntityPlayerMP)
			((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
	}
}

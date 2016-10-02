package com.bafomdad.realfilingcabinet.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.ItemStackHandler;

import com.bafomdad.realfilingcabinet.LogRFC;
import com.bafomdad.realfilingcabinet.api.common.IFolder;
import com.bafomdad.realfilingcabinet.api.helper.UpgradeHelper;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.network.VanillaPacketDispatcher;
import com.bafomdad.realfilingcabinet.utils.AutocraftingUtils;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;

public class InventoryRFC extends ItemStackHandler {
	
	final TileEntityRFC tile;

	public InventoryRFC(TileEntityRFC tile, int size) {
		
		this.tile = tile;
		setSize(size);
	}
	
	public void copyInv(InventoryRFC inv) {
		
		for (int i = 0; i < inv.stacks.length; i++) {
			stacks[i] = inv.getTrueStackInSlot(i);
		}
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		
		LogRFC.debug("Transfer stack: " + stack + " / Stack in slot: " + getStackInSlot(slot) + " / True stack in slot: "  + getTrueStackInSlot(slot) + " / Slot #" + slot + " / Simulating: " + simulate);
		
		if (tile.isCabinetLocked() || slot == 8)
			return stack;
		
        if (stack == null || stack.stackSize == 0)
            return null;

        validateSlotIndex(slot);

        if (StorageUtils.simpleFolderMatch(tile, stack) != -1)
        {
        	slot = StorageUtils.simpleFolderMatch(tile, stack);
        	if (!simulate) {
        		ItemFolder.add(stacks[slot], stack.stackSize);
        		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile.getWorld(), tile.getPos());
        		
            	return null;
        	}
        	return null;
        }
        return stack;
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		
		LogRFC.debug("Extraction slot: " + slot + " / Extraction amount: " + amount + " / " + simulate);
		
		ItemStack stackFolder = this.getStackFromFolder(slot);
		if (stackFolder == null || tile.isCabinetLocked() || UpgradeHelper.getUpgrade(tile, StringLibs.TAG_CRAFT) != null)
			return null;
		
		if (tile.hasItemFrame() && tile.getFilter() == null)
			return null;
		
		if (tile.getFilter() != null)
		{
			int i = StorageUtils.simpleFolderMatch(tile, tile.getFilter());
			if (i != -1 && slot == i)
			{
				stackFolder = this.getStackFromFolder(i);
				long filterCount = ItemFolder.getFileSize(getTrueStackInSlot(i));
				if (filterCount == 0)
					return null;
				
				long filterExtract = Math.min(stackFolder.getMaxStackSize(), filterCount);
				amount = Math.min((int)filterExtract, amount);
				
				if (!simulate && !UpgradeHelper.isCreative(tile)) {
					ItemFolder.remove(getTrueStackInSlot(i), amount);
		    		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile.getWorld(), tile.getPos());
				}
				stackFolder.stackSize = amount;
				return stackFolder.copy();
			}
			return null;
		}
		long count = ItemFolder.getFileSize(getTrueStackInSlot(slot));
		if (count == 0)
			return null;
		
		long extract = Math.min(stackFolder.getMaxStackSize(), count);
		amount = Math.min((int)extract, amount);
		
		if (!simulate && !UpgradeHelper.isCreative(tile)) {
			ItemFolder.remove(getTrueStackInSlot(slot), amount);
    		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile.getWorld(), tile.getPos());
		}
		
		stackFolder.stackSize = amount;
		return stackFolder.copy();
	}
	
	@Override
	protected void onContentsChanged(int slot) {
		
		super.onContentsChanged(slot);
		if (tile != null)
			tile.markDirty();
	}
	
	public ItemStack getTrueStackInSlot(int slot) {
		
		return stacks[slot];
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		
		validateSlotIndex(slot);
		
		if ((stacks[slot] != null && !(stacks[slot].getItem() instanceof IFolder)) || slot == 8)
			return null;
		
		ItemStack stackFolder = getStackFromFolder(slot);
		if (stackFolder != null)
		{
			long count = ItemFolder.getFileSize(getTrueStackInSlot(slot));
			if (count == 0)
				return null;
			
			long extract = Math.min(stackFolder.getMaxStackSize(), count);
			stackFolder.stackSize = (int)extract;
		}
		return stackFolder;
	}
	
	public ItemStack getStackFromFolder(int slot) {
		
		ItemStack folder = getTrueStackInSlot(slot);
		if (folder != null && folder.getItem() instanceof IFolder)
		{
			ItemStack stack = (ItemStack)ItemFolder.getObject(folder);
			if (stack != null) {
				return stack.copy();
			}
		}
		return null;
	}
	
	public ItemStack[] getStacks() {
		
		return stacks;
	}
	
	public TileEntityRFC getTile() {
		
		return tile;
	}
}

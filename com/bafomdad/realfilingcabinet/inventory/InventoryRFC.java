package com.bafomdad.realfilingcabinet.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.ItemStackHandler;

import com.bafomdad.realfilingcabinet.LogRFC;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.network.VanillaPacketDispatcher;
import com.bafomdad.realfilingcabinet.utils.AutocraftingUtils;
import com.bafomdad.realfilingcabinet.utils.DurabilityUtils;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;

public class InventoryRFC extends ItemStackHandler {
	
	final TileEntityRFC tile;

	public InventoryRFC(TileEntityRFC tile, int size) {
		
		this.tile = tile;
		setSize(size);
	}
	
	public void copyInv(InventoryRFC inv) {
		
		for (int i = 0; i < inv.stacks.size(); i++) {
			stacks.set(i, inv.getTrueStackInSlot(i));
		}
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		
		LogRFC.debug("Transfer stack: " + stack + " / Stack in slot: " + getStackInSlot(slot) + " / True stack in slot: "  + getTrueStackInSlot(slot) + " / Slot #" + slot + " / Simulating: " + simulate);
		
		if (tile.isCabinetLocked() || slot == 8)
			return stack;
		
        if (stack.isEmpty() || stack.getCount() == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        if (!stacks.get(slot).isEmpty() && stacks.get(slot).getItemDamage() == 2 && DurabilityUtils.matchDurability(tile, stack, slot, simulate)) {
        	if (!simulate) {
        		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile.getWorld(), tile.getPos());
        	}
        	return ItemStack.EMPTY;
        }
        if (StorageUtils.simpleFolderMatch(tile, stack) != -1) {
        	slot = StorageUtils.simpleFolderMatch(tile, stack);
        	if (!simulate) {
        		ItemFolder.add(stacks.get(slot), stack.getCount());
        		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile.getWorld(), tile.getPos());
        	}
        	return ItemStack.EMPTY;
        }
        return stack;
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		
		LogRFC.debug("Extraction slot: " + slot + " / Extraction amount: " + amount + " / " + simulate);
		
		if (ItemFolder.getObject(stacks.get(slot)) instanceof ItemStack) {
			ItemStack stackFolder = this.getStackFromFolder(slot);
			if (stackFolder.isEmpty() || tile.isCabinetLocked() || UpgradeHelper.getUpgrade(tile, StringLibs.TAG_CRAFT) != null)
				return ItemStack.EMPTY;
			
			if (tile.hasItemFrame() && tile.getFilter().isEmpty())
				return ItemStack.EMPTY;
			
			if (!tile.getFilter().isEmpty()) {
				int i = StorageUtils.simpleFolderMatch(tile, tile.getFilter());
				if (i != -1 && slot == i) {
					stackFolder = this.getStackFromFolder(i);
					long filterCount = ItemFolder.getFileSize(getTrueStackInSlot(i));
					if (filterCount == 0)
						return ItemStack.EMPTY;
					
					long filterExtract = Math.min(stackFolder.getMaxStackSize(), filterCount);
					amount = Math.min((int)filterExtract, amount);
					
					if (!simulate && !UpgradeHelper.isCreative(tile)) {
						ItemFolder.remove(getTrueStackInSlot(i), amount);
			    		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile.getWorld(), tile.getPos());
					}
					stackFolder.setCount(amount);
					return stackFolder.copy();
				}
				return ItemStack.EMPTY;
			}
			long count = ItemFolder.getFileSize(getTrueStackInSlot(slot));
			if (count == 0)
				return ItemStack.EMPTY;
			
			long extract = Math.min(stackFolder.getMaxStackSize(), count);
			amount = Math.min((int)extract, amount);
			
			if (!simulate && !UpgradeHelper.isCreative(tile)) {
				ItemFolder.remove(getTrueStackInSlot(slot), amount);
	    		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile.getWorld(), tile.getPos());
			}
			stackFolder.setCount(amount);
			return stackFolder.copy();
		}	
		return ItemStack.EMPTY;
	}
	
	@Override
	protected void onContentsChanged(int slot) {
		
		super.onContentsChanged(slot);
		if (tile != null)
			tile.markDirty();
	}
	
	public ItemStack getTrueStackInSlot(int slot) {
		
		if (slot >= 0)
			return stacks.get(slot);
		
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		
		validateSlotIndex(slot);
		
		if ((!stacks.get(slot).isEmpty() && !(stacks.get(slot).getItem() instanceof IFolder)) || slot == 8)
			return ItemStack.EMPTY;
		
		ItemStack stackFolder = getStackFromFolder(slot);
		if (!stackFolder.isEmpty()) {
			long count = ItemFolder.getFileSize(getTrueStackInSlot(slot));
			if (count == 0)
				return ItemStack.EMPTY;
			
			long extract = Math.min(Integer.MAX_VALUE - 1, count);
			stackFolder.setCount((int)extract);
		}
		return stackFolder;
	}
	
	public ItemStack getStackFromFolder(int slot) {
		
		ItemStack folder = getTrueStackInSlot(slot);
		if (ItemFolder.getObject(folder) == null)
			return ItemStack.EMPTY;
		
		if (!folder.isEmpty() && folder.getItem() instanceof IFolder) {
			if (ItemFolder.getObject(folder) instanceof ItemStack) {
				ItemStack stack = (ItemStack)ItemFolder.getObject(folder);
				if (!stack.isEmpty()) {
					return stack.copy();
				}
			}
			if (UpgradeHelper.getUpgrade(tile, StringLibs.TAG_FLUID) != null && ItemFolder.getObject(folder) instanceof FluidStack) {
				ItemStack bucket = FluidUtil.getFilledBucket((FluidStack)ItemFolder.getObject(folder));
				if (!bucket.isEmpty())
					return bucket.copy();
			}
		}
		return ItemStack.EMPTY;
	}
	
	public NonNullList<ItemStack> getStacks() {
		
		return stacks;
	}
	
	public TileEntityRFC getTile() {
		
		return tile;
	}
}

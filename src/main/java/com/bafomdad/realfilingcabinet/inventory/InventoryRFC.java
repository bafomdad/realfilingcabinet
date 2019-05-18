package com.bafomdad.realfilingcabinet.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.network.VanillaPacketDispatcher;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;

public class InventoryRFC extends ItemStackHandler {
	
	final TileEntityRFC tile;

	public InventoryRFC(TileEntityRFC tile, int size) {
		
		this.tile = tile;
		setSize(size);
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		
		if (tile.isCabinetLocked() || stack.isEmpty()) return stack;
		
		if (!simulate)
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile.getWorld(), tile.getPos());
		
		Object obj = FolderUtils.get(stacks.get(slot)).insert(stack, simulate);
		if (!(obj instanceof ItemStack)) return stack;
		
		return (ItemStack)obj;
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		
		if (tile.isCabinetLocked()) return ItemStack.EMPTY;
		
		ItemStack filter = tile.getFilter();
		if (!filter.isEmpty()) {
			for (int i = 0; i < this.getSlots(); i++) {
				ItemStack folderStack = getStackFromFolder(i);
				if (ItemStack.areItemsEqual(folderStack, filter)) {
					if (!simulate)
						VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile.getWorld(), tile.getPos());
					return (ItemStack)FolderUtils.get(stacks.get(i)).extract(amount, simulate);
				}
			}
		}
		return ItemStack.EMPTY;
	}
	
	public TileEntityRFC getTile() {
		
		return tile;
	}
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		
		if (stacks.get(slot).isEmpty() || !(stacks.get(slot).getItem() instanceof IFolder)) return ItemStack.EMPTY;
		
		ItemStack stackFolder = getStackFromFolder(slot);
		if (!stackFolder.isEmpty()) {
			if (FolderUtils.get(stacks.get(slot)).getFileSize() <= 0)
				return ItemStack.EMPTY;
		}
		return stackFolder;
	}
	
	public ItemStack getStackFromFolder(int slot) {
		 
		ItemStack folder = stacks.get(slot);
		Object obj = FolderUtils.get(folder).getObject();
		return (obj instanceof ItemStack) ? (ItemStack)obj : ItemStack.EMPTY;
	}
	
	public ItemStack getFolder(int slot) {
		
		if (slot >= 0) return stacks.get(slot);
		
		return ItemStack.EMPTY;
	}
}

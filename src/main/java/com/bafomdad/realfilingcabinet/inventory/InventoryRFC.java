package com.bafomdad.realfilingcabinet.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.ItemStackHandler;

import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.bafomdad.realfilingcabinet.network.VanillaPacketDispatcher;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;
import com.bafomdad.realfilingcabinet.utils.OreDictUtils;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;

public class InventoryRFC extends ItemStackHandler {
	
	final TileEntityRFC tile;

	public InventoryRFC(TileEntityRFC tile, int size) {
		
		this.tile = tile;
		setSize(size);
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		
		if (tile.isCabinetLocked() || stack.isEmpty()) return stack;
		
		Object obj = FolderUtils.get(stacks.get(slot)).insert(stack, simulate);
		if (!(obj instanceof ItemStack)) {
			return stack;
		}
		if (!simulate)
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile.getWorld(), tile.getPos());
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
					Object obj = FolderUtils.get(stacks.get(i)).extract(amount, simulate);
					if (obj instanceof ItemStack) {
						if (!simulate)
							VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile.getWorld(), tile.getPos());
						return (ItemStack)obj;
					}
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
			long count = FolderUtils.get(stacks.get(slot)).getFileSize();
			if (count <= 0)
				return ItemStack.EMPTY;
			
			long extract = Math.min(Integer.MAX_VALUE - 1, count);
			stackFolder.setCount((int)extract);
		}
		return stackFolder;
	}
	
	public ItemStack getStackFromFolder(int slot) {
		 
		ItemStack folder = stacks.get(slot);
		Object obj = FolderUtils.get(folder).getObject();
		if (obj instanceof FluidStack && tile instanceof TileFilingCabinet && !UpgradeHelper.getUpgrade((TileFilingCabinet)tile, StringLibs.TAG_FLUID).isEmpty()) {
			ItemStack bucket = FluidUtil.getFilledBucket((FluidStack)obj);
			if (!bucket.isEmpty())
				return bucket;
		}
		return (obj instanceof ItemStack) ? (ItemStack)obj : ItemStack.EMPTY;
	}
	
	public ItemStack getFolder(int slot) {
		
		if (slot >= 0) return stacks.get(slot);
		
		return ItemStack.EMPTY;
	}
}

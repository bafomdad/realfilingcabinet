package com.bafomdad.realfilingcabinet.inventory;

import javax.annotation.Nonnull;

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
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.network.VanillaPacketDispatcher;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;
import com.bafomdad.realfilingcabinet.utils.OreDictUtils;
import com.bafomdad.realfilingcabinet.utils.SmeltingUtils;
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

		boolean oreDict = tile instanceof TileFilingCabinet && !UpgradeHelper.getUpgrade((TileFilingCabinet)tile, StringLibs.TAG_OREDICT).isEmpty();
		if (oreDict) {
			OreDictUtils.recreateOreDictionary(stack);
			if (OreDictUtils.hasOreDict()) {
				oreDict = OreDictUtils.areItemsEqual(stack, this.getStackFromFolder(slot));
			}
		}
		// this is to skip over the hopper's simulated insertion. even if the insertion is successful,
		// on a oredictionaried item, it still fails in the end since the hopper still checks if
		// the two items are equal anyway
		simulate = (oreDict) ? false : simulate;
		Object obj = FolderUtils.get(stacks.get(slot)).insert(stack, simulate, oreDict);
		if (obj == null) {
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
		boolean hasFilter = tile.getItemFrame() != null;
		boolean creative = tile instanceof TileFilingCabinet && UpgradeHelper.isCreative((TileFilingCabinet)tile);
		
		if (!filter.isEmpty()) {
			boolean oreDict = tile instanceof TileFilingCabinet && !UpgradeHelper.getUpgrade((TileFilingCabinet)tile, StringLibs.TAG_OREDICT).isEmpty();
			if (oreDict) {
				OreDictUtils.recreateOreDictionary(filter);
				oreDict = OreDictUtils.hasOreDict();
			}
			for (int i = 0; i < this.getSlots(); i++) {
				ItemStack folderStack = getStackFromFolder(i);
				if (oreDict) {
					if (OreDictUtils.areItemsEqual(filter, folderStack)) {
						// break the loop if the found item and the requested slot doesn't match up if the filter is present
						if (hasFilter && slot != i) break;
						Object obj = FolderUtils.get(stacks.get(i)).extract(amount, simulate, creative);
						if (obj instanceof ItemStack) {
							if (!simulate)
								VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile.getWorld(), tile.getPos());
							return new ItemStack(filter.getItem(), ((ItemStack)obj).getCount(), filter.getItemDamage());
						}
					}
				}
				if (ItemStack.areItemsEqual(folderStack, filter)) {
					// break the loop if the found item and the requested slot doesn't match up if the filter is present
					if (hasFilter && slot != i) break;
					Object obj = FolderUtils.get(stacks.get(i)).extract(amount, simulate, creative);
					if (obj instanceof ItemStack) {
						if (!simulate)
							VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile.getWorld(), tile.getPos());
						return (ItemStack)obj;
					}
				}
			}
		}
		if (!hasFilter) {
			Object obj = FolderUtils.get(stacks.get(slot)).extract(amount, simulate, creative);
			if (obj instanceof ItemStack) {
				if (!simulate)
					VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile.getWorld(), tile.getPos());
				return (ItemStack)obj;
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
		ItemStack copystack = new ItemStack(stackFolder.getItem(), 1, stackFolder.getItemDamage());
		if (!stackFolder.isEmpty()) {
			if (stackFolder.hasTagCompound())
				copystack.setTagCompound(stackFolder.getTagCompound());
			
			long count = FolderUtils.get(stacks.get(slot)).getFileSize();
			if (count <= 0)
				return ItemStack.EMPTY;

			long extract = Math.min(Integer.MAX_VALUE - 1, count);
//			long extract = Math.min(stackFolder.getMaxStackSize(), count);
			copystack.setCount((int)extract);
		}
		return copystack;
	}
	
    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
    	
        validateSlotIndex(slot);
        this.stacks.set(slot, stack);
        if (stack.isEmpty()) {
            if (tile instanceof TileFilingCabinet && !tile.getWorld().isRemote && SmeltingUtils.isSmelting((TileFilingCabinet)tile)) {
            	SmeltingUtils.removeSmeltingJob((TileFilingCabinet)tile, slot);
            }
        }
        onContentsChanged(slot);
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

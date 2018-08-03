package com.bafomdad.realfilingcabinet.integration.storagedrawers;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;

@Optional.Interface(iface = "com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer", modid = RealFilingCabinet.STORAGEDRAWERS, striprefs = true)
public class CabinetData implements IDrawer {
	
	TileEntityRFC tile;
	int slot;
	
	public CabinetData(TileEntityRFC tile, int slot) {
		
		this.tile = tile;
		this.slot = slot;
	}

	@Override
	public ItemStack getStoredItemPrototype() {

		if (!(ItemFolder.getObject(tile.getInventory().getTrueStackInSlot(slot)) instanceof ItemStack)) return ItemStack.EMPTY;

		return tile.getInventory().getStackFromFolder(slot);
	}

	@Override
	public IDrawer setStoredItem(ItemStack prototype) {

		return this;
	}

	@Override
	public int getStoredItemCount() {
		
		return (int)Math.min(Integer.MAX_VALUE, ItemFolder.getFileSize(tile.getInventory().getTrueStackInSlot(slot)));
	}

	@Override
	public void setStoredItemCount(int amount) {

		ItemFolder.setFileSize(tile.getInventory().getTrueStackInSlot(slot), amount);
	}

	@Override
	public int getMaxCapacity(ItemStack prototype) {

		return 0;
	}

	@Override
	public int getRemainingCapacity() {
		
		long size = ItemFolder.getFileSize(tile.getInventory().getTrueStackInSlot(slot));
		if (size >= Integer.MAX_VALUE) return 0;
		return Integer.MAX_VALUE - (int)size;
	}

	@Override
	public boolean canItemBeStored(ItemStack prototype, Predicate<ItemStack> matchPredicate) {

		ItemStack toMatch = tile.getInventory().getStackFromFolder(slot);
		return ItemStack.areItemsEqual(toMatch, prototype);
	}

	@Override
	public boolean canItemBeExtracted(ItemStack prototype, Predicate<ItemStack> matchPredicate) {

		ItemStack toMatch = tile.getInventory().getStackInSlot(slot);
		return ItemStack.areItemsEqual(toMatch, prototype);
	}

	@Override
	public boolean isEmpty() {

		return tile.getInventory().getTrueStackInSlot(slot).isEmpty();
	}
	
	@Override
    public int adjustStoredItemCount (int amount) {
        
		if (amount > 0) {
            int insert = Math.min(amount, getRemainingCapacity());
            setStoredItemCount(getStoredItemCount() + insert);
            return amount - insert;
        } else if (amount < 0) {
            int stored = getStoredItemCount();
            int destroy = Math.min(Math.abs(amount), getStoredItemCount());
            setStoredItemCount(stored - destroy);
//            if (destroy >= stored)
//            	return 0;
//            return amount + destroy;
            return destroy;
        } else {
            return 0;
        }
    }
}

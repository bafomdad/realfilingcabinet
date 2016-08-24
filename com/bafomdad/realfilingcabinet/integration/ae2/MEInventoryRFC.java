package com.bafomdad.realfilingcabinet.integration.ae2;

import java.util.Iterator;

import net.minecraft.item.ItemStack;

import com.bafomdad.realfilingcabinet.blocks.TileEntityRFC;
import com.bafomdad.realfilingcabinet.core.Utils;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;

public class MEInventoryRFC implements IMEInventory<IAEItemStack> {

	final TileEntityRFC tile;
	
	public MEInventoryRFC(TileEntityRFC tileRFC) {
		
		this.tile = tileRFC;
	}
	
	private int tileCount() {
		
		return tile.getSizeInventory() - 2;
	}
	
	@Override
	public IAEItemStack injectItems(IAEItemStack input, Actionable type, BaseActionSource src) {

		long itemsLeft = input.getStackSize();
		for (int i = 0; i < tileCount(); i++) {
			ItemStack folder = tile.getStackInSlot(i);
			if (folder == null)
				continue;
			
			ItemStack itemProto = ItemFolder.getStack(folder);
			if (itemProto != null && simpleMatch(itemProto, input)) {
				itemsLeft = injectItemsIntoRFC(folder, itemsLeft, type);
				
				if (itemsLeft == 0)
					return null;
			}
		}
		if (itemsLeft > 0) {
			IAEItemStack overflow = AEApi.instance().storage().createItemStack(input.getItemStack());
			overflow.setStackSize(itemsLeft);
			return overflow;
		}
		
		return input;
	}
	
	private long injectItemsIntoRFC(ItemStack folder, long itemCount, Actionable type) {
		
		int capacity = Integer.MAX_VALUE;
		int storedItems = ItemFolder.getFileSize(folder);
		
		int storableItems = capacity - storedItems;
		
		if (storableItems == 0)
			return itemCount;
		
		long remainder = Math.max(itemCount - storableItems, 0);
		storedItems += Math.min(itemCount, storableItems);
		
		if (type == Actionable.MODULATE)
			ItemFolder.setFileSize(folder, storedItems);
		
		return remainder;
	}

	@Override
	public IAEItemStack extractItems(IAEItemStack request, Actionable mode, BaseActionSource src) {

		long itemsLeft = request.getStackSize();
		for (int i = 0; i < tileCount(); i++) {
			ItemStack folder = tile.getStackInSlot(i);
			if (folder == null)
				continue;
			
			ItemStack stack = ItemFolder.getStack(folder);
			if (simpleMatch(stack, request))
			{
				int itemCount = ItemFolder.getFileSize(folder);
				
				if (itemsLeft > itemCount) {
					if (mode == Actionable.MODULATE)
						ItemFolder.setFileSize(folder, 0);
					itemsLeft -= itemCount;
				} else {
					if (mode == Actionable.MODULATE)
						ItemFolder.setFileSize(folder, itemCount - (int)itemsLeft);
					itemsLeft = 0;
					break;
				}
			}
		}
		if (itemsLeft < request.getStackSize()) {
			ItemStack fulfillment = request.getItemStack().copy();
			fulfillment.stackSize -= itemsLeft;
			return AEApi.instance().storage().createItemStack(fulfillment);
		}
		
		return null;
	}

	@Override
	public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> out) {
		
		for (int i = 0; i < tileCount(); i++) {
			ItemStack folder = tile.getStackInSlot(i);
			if (folder == null || ItemFolder.getStack(folder) == null)
				continue;
			
			ItemStack stack = new ItemStack(ItemFolder.getStack(folder).getItem(), ItemFolder.getFileSize(folder), ItemFolder.getFileMeta(folder));

			out.add(AEApi.instance().storage().createItemStack(stack.copy()));
		}
		return out;
	}

	@Override
	public StorageChannel getChannel() {

		return StorageChannel.ITEMS;
	}
	
	private boolean simpleMatch(ItemStack stack1, IAEItemStack stack2) {
		
		return stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage();
	}
}

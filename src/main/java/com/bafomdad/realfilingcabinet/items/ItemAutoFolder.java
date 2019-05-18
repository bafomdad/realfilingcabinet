package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.realfilingcabinet.LogRFC;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;

public class ItemAutoFolder extends ItemAbstractFolder implements IFolder<ItemStack> {
	
	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		
		ItemStack item = ItemStack.EMPTY;
		CapabilityFolder cap = FolderUtils.get(stack).getCap();
		if (cap == null) return item;
		
		long count = FolderUtils.get(stack).getFileSize();
		long extract = 0;
		if (count > 0 && cap.isItemStack())
			extract = Math.min(cap.getItemStack().getMaxStackSize(), count);
			
		item = stack.copy();
		FolderUtils.get(item).remove(extract);
		
		return item;
	}
	
	@Override
	public boolean hasContainerItem(ItemStack stack) {
		
		return !getContainerItem(stack).isEmpty();
	}

	@Override
	public ItemStack getEmptyFolder(ItemStack stack) {

		return new ItemStack(RFCItems.AUTOFOLDER);
	}

	@Override
	public ItemStack insertIntoFolder(ItemStack folder, ItemStack toInsert, boolean simulate) {

		CapabilityFolder cap = FolderUtils.get(folder).getCap();
		if (!ItemStack.areItemsEqual(toInsert, cap.getItemStack()) && !cap.getItemStack().isEmpty()) return toInsert;
		if (cap.getItemStack().isEmpty() && cap.setContents(toInsert) && !simulate) {
			toInsert.setCount(0);
			return ItemStack.EMPTY;
		}
		if (!simulate) {
			cap.setCount(cap.getCount() + toInsert.getCount());
			toInsert.setCount(0);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack extractFromFolder(ItemStack folder, long amount, boolean simulate) {

		CapabilityFolder cap = FolderUtils.get(folder).getCap();
		ItemStack items = cap.getItemStack();
		if (items.isEmpty()) return ItemStack.EMPTY;
		
		items.setCount((int)Math.min(cap.getCount(), items.getMaxStackSize()));
		
		if (!simulate)
			cap.setCount(cap.getCount() - items.getCount());

		return items;	
	}
}

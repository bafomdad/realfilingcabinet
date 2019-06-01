package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.realfilingcabinet.LogRFC;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.helpers.enums.FolderType;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;

public class ItemAutoFolder extends ItemAbstractFolder implements IFolder {
	
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
	public Object insertIntoFolder(ItemStack folder, Object toInsert, boolean simulate, boolean oreDict) {

		CapabilityFolder cap = FolderUtils.get(folder).getCap();
		if (toInsert instanceof ItemStack) {
			ItemStack stack = (ItemStack)toInsert;
			if (cap.isFluidStack()) return toInsert;
			if (!FolderUtils.allowableIngredient(stack)) return toInsert;
			if (stack.hasTagCompound() || stack.isItemDamaged()) return toInsert;
			if (!ItemStack.areItemsEqual((ItemStack)toInsert, cap.getItemStack()) && !oreDict && !cap.getItemStack().isEmpty()) return toInsert;

			if (cap.getItemStack().isEmpty() && cap.setContents(stack) && !simulate) {
				stack.setCount(0);
				return ItemStack.EMPTY;
			}
			if (!simulate) {
				cap.setCount(cap.getCount() + stack.getCount());
				stack.setCount(0);
			}
			return ItemStack.EMPTY;
		}
		if (toInsert instanceof FluidStack) {
			if (cap.isItemStack()) return null;
			if (cap.getFluidStack() == null && cap.setContents(toInsert) && simulate) {
				return (FluidStack)toInsert;
			}
			return FolderType.FLUID.insert(cap, toInsert, simulate, oreDict);
		}
		return toInsert;
	}

	@Override
	public Object extractFromFolder(ItemStack folder, long amount, boolean simulate) {

		CapabilityFolder cap = FolderUtils.get(folder).getCap();
		if (cap.isItemStack())
			return FolderType.NORMAL.extract(cap, amount, simulate);
		
		if (cap.isFluidStack())
			return FolderType.FLUID.extract(cap, amount, simulate);
			
		return null;	
	}
}

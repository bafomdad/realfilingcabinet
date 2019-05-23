package com.bafomdad.realfilingcabinet.crafting;

import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.helpers.enums.FolderType;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class FolderMergeRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting ic, World world) {

		boolean hostFolder = false;
		boolean mergeFolder = false;
		
		for (int i = 0; i < ic.getSizeInventory(); i++) {
			ItemStack stack = ic.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() == RFCItems.FOLDER && stack.getItemDamage() == FolderType.ENDER.ordinal())
					continue;
				if (stack.getItem() instanceof IFolder && !hostFolder)
					hostFolder = true;
				else if (!mergeFolder) {
					if (stack.getItem() instanceof IFolder)
						mergeFolder = true;
					else return false;
				}
			}
		}
		return hostFolder && mergeFolder;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting ic) {

		ItemStack item = ItemStack.EMPTY;
		int hostFolder = -1;
		int mergeFolder = -1;

		for (int i = 0; i < ic.getSizeInventory(); i++) {
			ItemStack stack = ic.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof IFolder)
					if (hostFolder == -1) hostFolder = i;
				if (hostFolder >= 0 && hostFolder != i)
					mergeFolder = i;
			}
		}
		if (hostFolder >= 0 && mergeFolder >= 0) {
			ItemStack host = ic.getStackInSlot(hostFolder);
			ItemStack toMerge = ic.getStackInSlot(mergeFolder);
			
			if (!ItemStack.areItemsEqual(host, toMerge)) return item;
			if (!FolderUtils.areContentsEqual(host, toMerge)) return item;
			
			long mergeCount = FolderUtils.get(host).getFileSize() + FolderUtils.get(toMerge).getFileSize();
			if (mergeCount > 0) {
				item = host.copy();
				FolderUtils.get(item).setFileSize(mergeCount);
			}
		}
		return item;
	}

	@Override
	public boolean canFit(int width, int height) {

		return width * height >= 2;
	}

	@Override
	public ItemStack getRecipeOutput() {

		return ItemStack.EMPTY;
	}
	
	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting ic) {
		
		NonNullList<ItemStack> ret = NonNullList.withSize(ic.getSizeInventory(), ItemStack.EMPTY);
		for (int i = 0; i < ret.size(); i++) {
			if (ic.getStackInSlot(i).getItem() instanceof IFolder)
				ret.set(i, ItemStack.EMPTY);
		}
		return ret;
	}

	@Override
	public boolean isDynamic() {
		
		return true;
	}
}

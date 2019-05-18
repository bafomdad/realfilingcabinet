package com.bafomdad.realfilingcabinet.crafting;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class FolderTapeRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting ic, World world) {

		boolean foundFolder = false;
		boolean foundItem = false;
		
		for (int i = 0; i < ic.getSizeInventory(); i++) {
			ItemStack stack = ic.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof IFolder)
					foundFolder = true;
				else if (!foundItem && stack.getItem() == RFCItems.WHITEOUTTAPE)
					foundItem = true;
				else return false;
			}
		}
		return foundFolder && foundItem;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting ic) {

		ItemStack item = ItemStack.EMPTY;
		int folder = -1;
		int tape = -1;
		
		for (int i = 0; i < ic.getSizeInventory(); i++) {
			ItemStack stack = ic.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof IFolder)
					folder = i;
				if (stack.getItem() == RFCItems.WHITEOUTTAPE)
					tape = i;
			}
		}
		if (folder >= 0 && tape >= 0) {
			ItemStack stack1 = ic.getStackInSlot(folder);
			if (!stack1.isEmpty() && FolderUtils.get(stack1).getFileSize() <= 0) {
				item = ((IFolder)stack1.getItem()).getEmptyFolder(stack1);
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
	public boolean isDynamic() {
		
		return true;
	}
	
	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting ic) {
		
		NonNullList<ItemStack> ret = NonNullList.withSize(ic.getSizeInventory(), ItemStack.EMPTY);
		for (int i = 0; i < ret.size(); i++) {
			if (ic.getStackInSlot(i).getItem() == RFCItems.WHITEOUTTAPE)
				ret.set(i, ic.getStackInSlot(i).getItem().getContainerItem(ic.getStackInSlot(i)));
			else
				ret.set(i, ItemStack.EMPTY);
		}
		return ret;
	}
}

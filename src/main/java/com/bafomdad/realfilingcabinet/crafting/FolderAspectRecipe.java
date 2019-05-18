package com.bafomdad.realfilingcabinet.crafting;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaContainerItem;

import com.bafomdad.realfilingcabinet.api.IEmptyFolder;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemAspectFolder;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class FolderAspectRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting ic, World worldIn) {

		boolean foundFolder = false;
		boolean foundAspect = false;
		
		for (int i = 0; i < ic.getSizeInventory(); i++) {
			ItemStack stack = ic.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() == RFCItems.FOLDER_ASPECT)
					foundFolder = true;
				else if (!foundAspect && stack.getItem() instanceof IEssentiaContainerItem)
					foundAspect = true;
				else return false;
			}
		}
		return foundFolder && foundAspect;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting ic) {

		ItemStack item = ItemStack.EMPTY;
		int folder = -1;
		int phial = -1;
		
		for (int i = 0; i < ic.getSizeInventory(); i++) {
			ItemStack stack = ic.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() == RFCItems.FOLDER_ASPECT && ItemAspectFolder.isAspectFolderEmpty(stack))
					folder = i;
				if (stack.getItem() instanceof IEssentiaContainerItem && stack.getItem() == RFCItems.PHIAL && stack.getItemDamage() == 1)
					phial = i;
			}
		}
		if (folder >= 0 && phial >= 0) {
			IEssentiaContainerItem container = (IEssentiaContainerItem)ic.getStackInSlot(phial).getItem();
			Aspect asp = container.getAspects(ic.getStackInSlot(phial)).getAspects()[0];
			int count = container.getAspects(ic.getStackInSlot(phial)).getAmount(asp);
			
			item = ic.getStackInSlot(folder).copy();
			ItemAspectFolder.setAspect(item, asp);
			ItemAspectFolder.setAspectCount(item, count);
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
}

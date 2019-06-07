package com.bafomdad.realfilingcabinet.crafting;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IEmptyFolder;
import com.bafomdad.realfilingcabinet.api.IBlockCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class FolderStorageRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting ic, World world) {

		boolean foundFolder = false;
		boolean foundItem = false;
		
		for (int i = 0; i < ic.getSizeInventory(); i++) {
			ItemStack stack = ic.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof IEmptyFolder)
					foundFolder = true;
				else if (!foundItem && FolderUtils.allowableIngredient(stack))
					foundItem = true;
				else return false;
			}
		}
		return foundFolder && foundItem;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting ic) {

		ItemStack item = ItemStack.EMPTY;
		int emptyFolder = -1;
		int recipeStack = -1;
		
		for (int i = 0; i < ic.getSizeInventory(); i++) {
			ItemStack stack = ic.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof IEmptyFolder)
					emptyFolder = i;
				else
					recipeStack = i;
			}
		}
		if (emptyFolder >= 0 && recipeStack >= 0) {
			ItemStack ingredient = ic.getStackInSlot(recipeStack);
			ItemStack folder = ic.getStackInSlot(emptyFolder);
			
			if (((IEmptyFolder)folder.getItem()).canRecipeTakeStack(folder, ingredient)) {
				item = ((IEmptyFolder)folder.getItem()).getFilledFolder(folder);
				if (!item.isEmpty())
					FolderUtils.get(item).setObject(ingredient);
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
			ItemStack stack = ic.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (!(stack.getItem() instanceof IEmptyFolder)) {
					ic.setInventorySlotContents(i, ItemStack.EMPTY);
					ret.set(i, ItemStack.EMPTY);
				}
			}
		}
		return ret;
	}
}

package com.bafomdad.realfilingcabinet.crafting;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class FolderStorageRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting ic, World world) {

		ItemStack foundFolder = null;
		boolean ingredient = false;
		
		for (int i = 0; i < ic.getSizeInventory(); i++)
		{
			ItemStack stack = ic.getStackInSlot(i);
			
			if (stack != null)
			{
				if (stack.getItem() == RealFilingCabinet.itemEmptyFolder /*|| (stack.getItem() == RealFilingCabinet.itemFolder && ItemFolder.getFileSize(stack) == 0)*/)
					foundFolder = stack;
				else if ((stack.getItem() instanceof ItemBlock || stack.getItem() instanceof Item) && (stack.getItem() != RealFilingCabinet.itemFolder))
					ingredient = true;
			}
		}
		return foundFolder != null && ingredient;
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting ic) {
		
		ItemStack folderStack = null;
		
		for (int i = 0; i < ic.getSizeInventory(); i++) 
		{
			ItemStack stack = ic.getStackInSlot(i);
			if (stack != null && this.allowableIngredient(stack))
			{
				folderStack = stack;
				break;
			}
		}
		if (folderStack != null)
		{
			ItemStack newFolder = new ItemStack(RealFilingCabinet.itemFolder, 1, 0);
			int damage = folderStack.getItemDamage();
			
			ItemFolder.setStack(newFolder, folderStack, damage);
			return newFolder;
		}
		return null;
	}

	@Override
	public int getRecipeSize() {

		return 10;
	}

	@Override
	public ItemStack getRecipeOutput() {

		return null;
	}
	
	private boolean allowableIngredient(ItemStack stack) {
		
		if (stack.getItem() instanceof ItemTool || stack.hasTagCompound() || stack.getItem() instanceof ItemArmor || stack.isItemStackDamageable())
			return false;
		else if (stack.getItem() == RealFilingCabinet.itemFolder || stack.getItem() == RealFilingCabinet.itemEmptyFolder || stack.getItem() == Item.getItemFromBlock(RealFilingCabinet.blockRFC))
			return false;
		
		return true;
	}
}

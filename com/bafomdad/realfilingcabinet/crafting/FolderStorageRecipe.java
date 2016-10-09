package com.bafomdad.realfilingcabinet.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;

public class FolderStorageRecipe extends ShapelessRecipes implements IRecipe {

	public static List inputs = new ArrayList();
	
	static
	{
		inputs.add(new ItemStack(RFCItems.emptyFolder));
	}
	public FolderStorageRecipe() {
		
		super(new ItemStack(RFCItems.folder, 1, 0), inputs);
	}
	
	@Override
	public boolean matches(InventoryCrafting ic, World world) {
		
		ArrayList list = new ArrayList(this.recipeItems);
		
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				
				ItemStack stack = ic.getStackInRowAndColumn(j, i);
				if (stack != null)
				{
					if (allowableIngredient(stack))
						list.add(stack);
					
					boolean flag = false;
					Iterator iter = list.iterator();
					
					while (iter.hasNext())
					{
						ItemStack stack1 = (ItemStack)iter.next();
						
						if (stack.getItem() == stack1.getItem() && (stack1.getItemDamage() == 32767 || stack.getItemDamage() == stack1.getItemDamage()))
						{
							flag = true;
							list.remove(stack1);
							break;
						}
					}
					if (!flag)
						return false;
				}
			}
		}
		return list.isEmpty();
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting ic) {
		
		int emptyFolder = -1;
		int recipestack = -1;
		
		for (int i = 0; i < ic.getSizeInventory(); i++) {
			
			ItemStack stack = ic.getStackInSlot(i);
			if (stack != null)
			{
				if (stack.getItem() == RFCItems.emptyFolder)
					emptyFolder = i;
				if (allowableIngredient(stack))
					recipestack = i;
			}
		}
		if (emptyFolder >= 0 && recipestack >= 0)
		{
			ItemStack stack1 = ic.getStackInSlot(recipestack);
			
			ItemStack newFolder = new ItemStack(RFCItems.folder);
			ItemFolder.setObject(newFolder, stack1);
			return newFolder;
		}
		return new ItemStack(RFCItems.emptyFolder);
	}
	
	private boolean allowableIngredient(ItemStack stack) {
		
		if (stack.getItem() instanceof ItemTool || stack.hasTagCompound() || stack.getItem() instanceof ItemArmor || stack.isItemStackDamageable())
			return false;
		else if (stack.getItem() == RFCItems.folder || stack.getItem() == RFCItems.emptyFolder || stack.getItem() == Item.getItemFromBlock(RFCBlocks.blockRFC))
			return false;
		
		return true;
	}
}

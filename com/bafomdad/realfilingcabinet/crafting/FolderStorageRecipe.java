package com.bafomdad.realfilingcabinet.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IEmptyFolder;
import com.bafomdad.realfilingcabinet.api.IFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.init.RFCBlocks;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

public class FolderStorageRecipe extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	private List<ItemStack> inputs;
	private ItemStack output;
	final String name;
	
	public FolderStorageRecipe(String name, ItemStack output, List<ItemStack> inputs) {
		
		this.output = output;
		this.inputs = inputs;
		this.name = name;
		this.setRegistryName(new ResourceLocation(RealFilingCabinet.MOD_ID, name));
	}
	
	@Override
	public boolean matches(InventoryCrafting ic, World world) {
		
		ArrayList list = new ArrayList(this.inputs);
		
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				
				ItemStack stack = ic.getStackInRowAndColumn(j, i);
				if (!stack.isEmpty())
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
			if (!stack.isEmpty())
			{
				if (stack.getItem() instanceof IEmptyFolder)
					emptyFolder = i;
				else
					recipestack = i;
			}
		}
		if (emptyFolder >= 0 && recipestack >= 0)
		{
			ItemStack stack1 = ic.getStackInSlot(recipestack);
			ItemStack folder = ic.getStackInSlot(emptyFolder);
			
			if ((folder.getItemDamage() == 0 && !stack1.getItem().isRepairable()) || (folder.getItemDamage() == 1 && stack1.getItem().isRepairable()))
			{
				int damage = 0;
				if (folder.getItemDamage() == 1)
					damage = 2;
				ItemStack newFolder = new ItemStack(RFCItems.folder, 1, damage);
				ItemFolder.setObject(newFolder, stack1);
				return newFolder;
			}
			else if (folder.getItemDamage() == 4 && stack1.hasTagCompound()) {
				ItemStack newFolder = new ItemStack(RFCItems.folder, 1, 5);
				ItemFolder.setObject(newFolder, stack1);
				return newFolder;
			}
		}
		return ItemStack.EMPTY;
	}
	
	private boolean allowableIngredient(ItemStack stack) {
		
		if (stack.getItem() instanceof IFolder || stack.getItem() instanceof IEmptyFolder || Block.getBlockFromItem(stack.getItem()) instanceof IFilingCabinet)
			return false;
		
		if (stack.getItem().isRepairable() && stack.getItemDamage() == 0)
			return true;
		else if (stack.getItem().isRepairable() && stack.getItemDamage() != 0)
			return false;
		
		return true;
	}

	@Override
	public boolean canFit(int width, int height) {

		return width >= 3 && height >= 3;
	}

	@Override
	public ItemStack getRecipeOutput() {

		return new ItemStack(RFCItems.emptyFolder);
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting ic) {

		return ForgeHooks.defaultRecipeGetRemainingItems(ic);
	}
}
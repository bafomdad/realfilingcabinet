package com.bafomdad.realfilingcabinet.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class FolderTapeRecipe extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	
	public static List inputs = new ArrayList();
	final String name;
	
	static
	{
		inputs.add(new ItemStack(RFCItems.whiteoutTape));
	}

	public FolderTapeRecipe(String name) {
		
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
					if (stack.getItem() instanceof IFolder || stack.isItemEnchanted())
						list.add(stack);
					
					boolean flag = false;
					Iterator iter = list.iterator();
					
					while (iter.hasNext())
					{
						ItemStack stack1 = (ItemStack)iter.next();
						
						if (stack.getItem() == stack1.getItem())
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
		
		int folder = -1;
		int tape = -1;
		int misc = -1;
		
		for (int i = 0; i < ic.getSizeInventory(); i++) {
			
			ItemStack stack = ic.getStackInSlot(i);
			
			if (!stack.isEmpty())
			{
				if (stack.getItem() == RFCItems.folder && stack.getItemDamage() != 1)
					folder = i;
				if (stack.getItem() == RFCItems.whiteoutTape)
					tape = i;
				if (stack.isItemEnchanted())
					misc = i;
			}
		}
		if (folder >= 0 && tape >= 0 && misc <= 0)
		{
			StorageUtils.checkTapeNBT(ic.getStackInSlot(folder), true);
			
			ItemStack stack2 = ic.getStackInSlot(folder);
			if (ItemFolder.getObject(stack2) != null && ItemFolder.getFileSize(stack2) == 0)
			{
				ItemStack stacky = ic.getStackInSlot(folder);
				return ((IFolder)stacky.getItem()).isFolderEmpty(stacky);
			}
		}
		else if (tape >= 0 && misc >= 0 && folder <= 0)
		{
			ItemStack stack3 = ic.getStackInSlot(misc).copy();
			stack3.setCount(1);
			EnchantmentHelper.setEnchantments(Collections.emptyMap(), stack3);
			return stack3;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height) {

		return width >= 3 && height >= 3;
	}

	@Override
	public ItemStack getRecipeOutput() {

		return new ItemStack(RFCItems.whiteoutTape);
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting ic) {

        NonNullList<ItemStack> ret = NonNullList.withSize(ic.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < ret.size(); i++) {
        	if (ic.getStackInSlot(i).getItem() == RFCItems.whiteoutTape)
        		ret.set(i, ic.getStackInSlot(i).getItem().getContainerItem(ic.getStackInSlot(i)));
        	else
        		ret.set(i, ItemStack.EMPTY);
        }
        return ret;
	}
}

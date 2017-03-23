package com.bafomdad.realfilingcabinet.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;

public class FolderTapeRecipe extends ShapelessRecipes implements IRecipe {
	
	public static List inputs = new ArrayList();
	
	static
	{
		inputs.add(new ItemStack(RFCItems.whiteoutTape));
	}

	public FolderTapeRecipe() {
		
		super(new ItemStack(RFCItems.emptyFolder), inputs);
	}
	
	@Override
	public boolean matches(InventoryCrafting ic, World world) {
		
		ArrayList list = new ArrayList(this.inputs);
		
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				
				ItemStack stack = ic.getStackInRowAndColumn(j, i);
				if (stack != ItemStack.EMPTY)
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
			ItemStack stack3 = ic.getStackInSlot(misc);

			return new ItemStack(stack3.getItem(), 1, stack3.getItemDamage());
		}
		return ItemStack.EMPTY;
	}
}

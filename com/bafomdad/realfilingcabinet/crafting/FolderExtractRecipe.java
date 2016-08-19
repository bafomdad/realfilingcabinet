package com.bafomdad.realfilingcabinet.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.TileEntityRFC;
import com.bafomdad.realfilingcabinet.core.StorageUtils;
import com.bafomdad.realfilingcabinet.core.Utils;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;

public class FolderExtractRecipe extends ShapelessRecipes implements IRecipe {
	
	public static List<ItemStack> input = new ArrayList();
	
	static
	{
		input.add(new ItemStack(RealFilingCabinet.itemFolder));
	}
	
	public FolderExtractRecipe() {
		
		super(new ItemStack(RealFilingCabinet.itemFolder), input);
	}
	
	@Override
	public boolean matches(InventoryCrafting ic, World world) {
		
		ArrayList list = new ArrayList(this.input);
		
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				
				ItemStack stack = ic.getStackInRowAndColumn(j, i);
				if (stack != null)
				{
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
		
		for (int i = 0; i < ic.getSizeInventory(); i++) {
			
			ItemStack stack = ic.getStackInSlot(i);
			if (stack != null)
			{
				folder = i;
			}
		}
		if (folder >= 0)
		{
			ItemStack stack = ic.getStackInSlot(folder);
			
			if (stack.getItem() == RealFilingCabinet.itemFolder && ItemFolder.getStack(stack) != null)
			{
				int count = ItemFolder.getFileSize(stack);
				if (count > 0)
				{
					ItemStack folderStack = ItemFolder.getStack(stack);
					int meta = folderStack.getItemDamage();
					
//					System.out.println("crafting stacksize: " + count);
					return new ItemStack(folderStack.getItem(), Math.min(64, count), meta);
				}
			}
		}
		return null;
	}
}


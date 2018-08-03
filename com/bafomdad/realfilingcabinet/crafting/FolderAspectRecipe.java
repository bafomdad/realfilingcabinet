package com.bafomdad.realfilingcabinet.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaContainerItem;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemAspectFolder;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class FolderAspectRecipe extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	
	public static List<ItemStack> inputs = new ArrayList();
	private ItemStack output;
	final String name;
	
	static
	{
		inputs.add(new ItemStack(RFCItems.aspectFolder, 1, 0));
	}
	
	public FolderAspectRecipe(String name) {
		
		this.name = name;
		this.setRegistryName(new ResourceLocation(RealFilingCabinet.MOD_ID, name));
	}
	
	@Override
	public boolean matches(InventoryCrafting ic, World world) {
		
		ArrayList list = new ArrayList(this.inputs);
		
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				
				ItemStack stack = ic.getStackInRowAndColumn(j, i);
				if (!stack.isEmpty()) {
					if (stack.getItem() instanceof IEssentiaContainerItem)
						list.add(stack);
					
					boolean flag = false;
					Iterator iter = list.iterator();
					
					while (iter.hasNext()) {
						ItemStack stack1 = (ItemStack)iter.next();
						
						if (stack.getItem() == stack1.getItem()) {
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
		int aspectItem = -1;
		
		for (int i = 0; i < ic.getSizeInventory(); i++) {
			ItemStack stack = ic.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() == RFCItems.aspectFolder && ItemAspectFolder.isAspectFolderEmpty(stack)) {
					folder = i;
				}
				if (stack.getItem() instanceof IEssentiaContainerItem && (stack.getItem() == ForgeRegistries.ITEMS.getValue(new ResourceLocation("thaumcraft", "phial")) && stack.getItemDamage() == 1)) {
					aspectItem = i;
				}
			}
		}
		if (folder >= 0 && aspectItem >= 0) {
			IEssentiaContainerItem container = (IEssentiaContainerItem)ic.getStackInSlot(aspectItem).getItem();
			Aspect asp = container.getAspects(ic.getStackInSlot(aspectItem)).getAspects()[0];
			int count = container.getAspects(ic.getStackInSlot(aspectItem)).getAmount(asp);
			ItemStack newFolder = new ItemStack(RFCItems.aspectFolder);
			ItemAspectFolder.setAspect(newFolder, asp);
			ItemAspectFolder.setAspectCount(newFolder, count);
			
			return newFolder;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height) {

		return width >= 3 && height >= 3;
	}

	@Override
	public ItemStack getRecipeOutput() {

		return new ItemStack(RFCItems.aspectFolder);
	}
}

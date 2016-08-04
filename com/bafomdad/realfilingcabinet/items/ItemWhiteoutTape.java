package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

public class ItemWhiteoutTape extends Item {

	public ItemWhiteoutTape() {
		
		setMaxStackSize(1);
		setMaxDamage(25);
	}
	
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean whatisthis) {
		
		list.add("Craft with any folder with 0 items or blocks");
		list.add("in it to return a empty folder for reuse");
	}
	
    public ItemStack getContainerItem(ItemStack stack) {
    	
    	ItemStack copy = stack.copy();
    	copy.setItemDamage(copy.getItemDamage() + 1);
    	
    	return copy;
    }
    
    public boolean hasContainerItem(ItemStack stack) {
    	
    	return getContainerItem(stack) != null && stack.getItem() == this;
    }
	
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
    	
    	return false;
    }
}

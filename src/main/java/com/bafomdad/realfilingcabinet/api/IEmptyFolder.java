package com.bafomdad.realfilingcabinet.api;

import net.minecraft.item.ItemStack;

public interface IEmptyFolder {

	public ItemStack getFilledFolder(ItemStack stack);
	
	public boolean canRecipeTakeStack(ItemStack folder, ItemStack recipeStack);
}

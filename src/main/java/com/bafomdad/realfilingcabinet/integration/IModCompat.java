package com.bafomdad.realfilingcabinet.integration;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;

public interface IModCompat {

	public String getModID();
	
	public boolean isConfigEnabled();
	
	public default void register() {}
	
	public default void registerBlocks(RegistryEvent.Register<Block> event) {}
	
	public default void registerItems(RegistryEvent.Register<Item> event) {}
	
	public default void registerRecipes(RegistryEvent.Register<IRecipe> event) {}
	
	public default void registerModels(ModelRegistryEvent event) {}
}

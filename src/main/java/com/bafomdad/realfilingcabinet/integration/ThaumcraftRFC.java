package com.bafomdad.realfilingcabinet.integration;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.integration.loaders.ThaumcraftLoader;

public class ThaumcraftRFC implements IModCompat {

	@Override
	public String getModID() {

		return "thaumcraft";
	}

	@Override
	public boolean isConfigEnabled() {

		return ConfigRFC.CompatConfig.tcIntegration;
	}
	
	@Override
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		
		ThaumcraftLoader.registerBlocks(event);
	}
	
	@Override
	public void registerItems(RegistryEvent.Register<Item> event) {
		
		ThaumcraftLoader.registerItems(event);
	}
	
	@Override
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		
		ThaumcraftLoader.registerRecipes(event);
	}
	
	@Override
	public void registerModels(ModelRegistryEvent event) {
		
		ThaumcraftLoader.registerModels(event);
	}
}

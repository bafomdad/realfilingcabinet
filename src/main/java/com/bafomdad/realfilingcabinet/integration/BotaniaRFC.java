package com.bafomdad.realfilingcabinet.integration;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.integration.loaders.BotaniaLoader;

public class BotaniaRFC implements IModCompat {

	@Override
	public String getModID() {

		return "botania";
	}

	@Override
	public boolean isConfigEnabled() {

		return ConfigRFC.CompatConfig.botaniaIntegration;
	}
	
	@Override
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		
		BotaniaLoader.registerBlocks(event);
	}
	
	@Override
	public void registerItems(RegistryEvent.Register<Item> event) {
		
		BotaniaLoader.registerItems(event);
	}
	
	@Override
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		
		BotaniaLoader.registerRecipes(event);
	}
	
	@Override
	public void registerModels(ModelRegistryEvent event) {
		
		BotaniaLoader.registerModels(event);
	}
}

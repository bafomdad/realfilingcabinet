package com.bafomdad.realfilingcabinet.integration;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.recipes.*;
import crafttweaker.mc1120.item.MCItemStack;
import crafttweaker.mc1120.recipes.RecipeConverter;

public class CraftTweakerRFC {

	/**
	 * this is an old test method. please don't use this. thanks
	 */
	public static boolean craftTweakered(IRecipe recipe) {
		
		return recipe.getGroup().split(":")[0].equals("crafttweaker");
	}
	
	public static IRecipe getTweakedRecipe(ItemStack result) {
	
		IRecipeManager manager = CraftTweakerAPI.recipes;
		List<ICraftingRecipe> recipeList = manager.getRecipesFor(new MCItemStack(result));
		if (!recipeList.isEmpty()) {
			ICraftingRecipe recipe = recipeList.get(0);
			if (recipe instanceof ShapelessRecipe)
				return RecipeConverter.convert((ShapelessRecipe)recipe, new ResourceLocation("crafttweaker", recipe.getName()));
			if (recipe instanceof ShapedRecipe)
				return RecipeConverter.convert((ShapedRecipe)recipe, new ResourceLocation("crafttweaker", recipe.getName()));
		}
		return null;
	}
}

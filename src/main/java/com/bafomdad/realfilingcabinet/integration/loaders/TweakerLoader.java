package com.bafomdad.realfilingcabinet.integration.loaders;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.recipes.ICraftingRecipe;
import crafttweaker.api.recipes.IRecipeManager;
import crafttweaker.api.recipes.ShapedRecipe;
import crafttweaker.api.recipes.ShapelessRecipe;
import crafttweaker.mc1120.item.MCItemStack;
import crafttweaker.mc1120.recipes.RecipeConverter;

public class TweakerLoader {

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

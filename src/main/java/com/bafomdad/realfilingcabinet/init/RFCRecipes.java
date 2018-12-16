package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.crafting.*;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializers;

/**
 * Created by bafomdad on 12/13/2018.
 */
public class RFCRecipes {

    public static RecipeSerializers.Dummy<FolderStorageRecipe> FOLDER_STORAGE_RECIPE;
    public static RecipeSerializers.Dummy<FolderExtractRecipe> FOLDER_EXTRACT_RECIPE;

    public static void init() {

        registerSerializer(FOLDER_STORAGE_RECIPE, "crafting_special_folderstorage", new FolderStorageRecipe("normalfolderstorage", new ItemStack(RFCItems.FOLDER), new ItemStack(RFCItems.EMPTYFOLDER)));
        registerSerializer(FOLDER_EXTRACT_RECIPE, "crafting_special_folderextract", new FolderExtractRecipe("normalfolderextract", new ItemStack(RFCItems.FOLDER)));
    }

    private static void registerSerializer(RecipeSerializers.Dummy<? extends Recipe> serializer, String jsonRecipeName, Recipe recipe) {

        serializer = (RecipeSerializers.Dummy)(RecipeSerializers.register(new RecipeSerializers.Dummy(jsonRecipeName, (var1) -> { return recipe; })));
    }
}

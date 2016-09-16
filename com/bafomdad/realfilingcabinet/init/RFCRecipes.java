package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.crafting.*;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

public class RFCRecipes {

	public static void init() {
		
		GameRegistry.addRecipe(new ItemStack(RFCItems.emptyFolder, 8, 0), new Object[] { "P  ", "P  ", "PPP", 'P', Items.PAPER });
		GameRegistry.addShapedRecipe(new ItemStack(RFCBlocks.blockRFC), new Object[] { "III", "ICI", "III", 'I', Blocks.IRON_BARS, 'C', Blocks.CHEST });
//		GameRegistry.addShapedRecipe(new ItemStack(RFCBlocks.blockRFC), new Object[] { "III", "ICI", "III", 'I', Blocks.IRON_BARS, 'C', Blocks.TRAPPED_CHEST });
		GameRegistry.addRecipe(new ItemStack(RFCItems.whiteoutTape), new Object[] { " P ",  "PSP", "BP ", 'P', Items.PAPER, 'S', Items.SLIME_BALL, 'B', new ItemStack(Items.DYE, 1, 15) });
		GameRegistry.addRecipe(new ItemStack(RFCItems.magnifyingGlass), new Object[] { "G  ", " S ", "   ", 'S', Items.STICK, 'G', Blocks.GLASS_PANE });
		GameRegistry.addRecipe(new ItemStack(RFCItems.filter), new Object[] { "RRR", " R ", " R ", 'R', new ItemStack(Items.DYE, 1, 1) });
		
		if (ConfigRFC.craftingUpgrade)
		{
			GameRegistry.addRecipe(new ItemStack(RFCItems.upgrades, 1, 1), new Object[] { "LCL", "CFC", "LCL", 'L',  Blocks.LOG, 'C', Blocks.CRAFTING_TABLE, 'F', RFCBlocks.blockRFC });
			GameRegistry.addRecipe(new ItemStack(RFCItems.upgrades, 1, 1), new Object[] { "LCL", "CFC", "LCL", 'L',  Blocks.LOG2, 'C', Blocks.CRAFTING_TABLE, 'F', RFCBlocks.blockRFC });
		}
		if (ConfigRFC.enderUpgrade)
			GameRegistry.addRecipe(new ItemStack(RFCItems.upgrades, 1, 2), new Object[] { "OEO", "EFE", "OEO", 'O', Blocks.OBSIDIAN, 'E', Items.ENDER_EYE, 'F', RFCBlocks.blockRFC });
		if (ConfigRFC.oreDictUpgrade)
			GameRegistry.addRecipe(new ItemStack(RFCItems.upgrades, 1, 3), new Object[] { "GOG", "SFB", "GJG", 'G', Blocks.GLASS, 'O', new ItemStack(Blocks.PLANKS, 1, 0), 'S', new ItemStack(Blocks.PLANKS, 1, 1), 'B', new ItemStack(Blocks.PLANKS, 1, 2), 'J', new ItemStack(Blocks.PLANKS, 1, 3), 'F', RFCBlocks.blockRFC });
		
		GameRegistry.addRecipe(new FolderExtractRecipe());
		GameRegistry.addRecipe(new FolderStorageRecipe());
		GameRegistry.addRecipe(new FolderMergeRecipe());
		GameRegistry.addRecipe(new FolderTapeRecipe());
		
		RecipeSorter.register("FolderExtractRecipe", FolderExtractRecipe.class, Category.SHAPELESS, "");
		RecipeSorter.register("FolderStorageRecipe", FolderStorageRecipe.class, Category.SHAPELESS, "");
		RecipeSorter.register("FolderMergeRecipe", FolderMergeRecipe.class, Category.SHAPELESS, "");
		RecipeSorter.register("FolderTapeRecipe", FolderTapeRecipe.class, Category.SHAPELESS, "");
	}
}
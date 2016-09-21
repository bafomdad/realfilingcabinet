package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.crafting.*;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RFCRecipes {

	public static void init() {
		
		GameRegistry.addRecipe(new ItemStack(RFCItems.emptyFolder, 8, 0), new Object[] { "P  ", "P  ", "PPP", 'P', Items.PAPER });
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RFCBlocks.blockRFC), "III", "ICI", "III", 'I', Blocks.IRON_BARS, 'C', "chest" ));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RFCItems.whiteoutTape), " P ",  "PSP", "BP ", 'P', Items.PAPER, 'S', "slimeball", 'B', new ItemStack(Items.DYE, 1, 15) ));
		GameRegistry.addRecipe(new ItemStack(RFCItems.magnifyingGlass), new Object[] { "G  ", " S ", "   ", 'S', Items.STICK, 'G', Blocks.GLASS_PANE });
		GameRegistry.addRecipe(new ItemStack(RFCItems.filter), new Object[] { "RRR", " R ", " R ", 'R', new ItemStack(Items.DYE, 1, 1) });
		
		GameRegistry.addRecipe(new ItemStack(RFCItems.keys, 1, 0), new Object[] { "  N", " N ", "I  ", 'N', Items.GOLD_NUGGET, 'I', Items.GOLD_INGOT });
		GameRegistry.addRecipe(new ItemStack(RFCItems.keys, 1, 1), new Object[] { "  C", " C ", "B  ", 'C', Items.CLAY_BALL, 'B', Items.BRICK });
		
		if (ConfigRFC.craftingUpgrade)
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RFCItems.upgrades, 1, 1), "LCL", "CFC", "LCL", 'L', "logWood", 'C', "workbench", 'F', RFCBlocks.blockRFC ));
		}
		if (ConfigRFC.enderUpgrade)
			GameRegistry.addRecipe(new ItemStack(RFCItems.upgrades, 1, 2), new Object[] { "OEO", "EFE", "OEO", 'O', Blocks.OBSIDIAN, 'E', Items.ENDER_EYE, 'F', RFCBlocks.blockRFC });
		if (ConfigRFC.oreDictUpgrade)
			GameRegistry.addRecipe(new ItemStack(RFCItems.upgrades, 1, 3), new Object[] { "GOG", "SFB", "GJG", 'G', Blocks.GLASS, 'O', new ItemStack(Blocks.PLANKS, 1, 0), 'S', new ItemStack(Blocks.PLANKS, 1, 1), 'B', new ItemStack(Blocks.PLANKS, 1, 2), 'J', new ItemStack(Blocks.PLANKS, 1, 3), 'F', RFCBlocks.blockRFC });
		
		if (ConfigRFC.nametagRecipe)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.NAME_TAG), " PP", " BP", "S  ", 'P', Items.PAPER, 'B', "slimeball", 'S', Items.STRING ));
		
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
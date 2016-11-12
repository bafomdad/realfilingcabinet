package com.bafomdad.realfilingcabinet.init;

import java.util.ArrayList;
import java.util.List;

import scala.actors.threadpool.Arrays;

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
		GameRegistry.addRecipe(new ItemStack(RFCItems.emptyFolder, 4, 1), new Object[] { "P  ", "PA ", "PPP", 'P', Items.PAPER, 'A', Items.IRON_PICKAXE });
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RFCBlocks.blockRFC), "III", "ICI", "III", 'I', Blocks.IRON_BARS, 'C', "chest" ));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RFCItems.whiteoutTape), " P ",  "PSP", "BP ", 'P', Items.PAPER, 'S', "slimeball", 'B', new ItemStack(Items.DYE, 1, 15) ));
		GameRegistry.addRecipe(new ItemStack(RFCItems.magnifyingGlass), new Object[] { "G  ", " S ", "   ", 'S', Items.STICK, 'G', Blocks.GLASS_PANE });
		GameRegistry.addRecipe(new ItemStack(RFCItems.filter), new Object[] { "RRR", " R ", " R ", 'R', new ItemStack(Items.DYE, 1, 1) });
		
		if (ConfigRFC.binBlock)
			GameRegistry.addRecipe(new ItemStack(RFCBlocks.blockBin), new Object[] { "ccc", "BSB", "CCC", 'c', new ItemStack(Blocks.STONE_SLAB, 1, 3), 'C', Blocks.COBBLESTONE, 'B', Blocks.BRICK_BLOCK, 'S', new ItemStack(Blocks.STONE_SLAB, 1, 4) });
		
		GameRegistry.addRecipe(new ItemStack(RFCItems.keys, 1, 0), new Object[] { "  N", " N ", "I  ", 'N', Items.GOLD_NUGGET, 'I', Items.GOLD_INGOT });
		GameRegistry.addRecipe(new ItemStack(RFCItems.keys, 1, 1), new Object[] { "  C", " C ", "B  ", 'C', Items.CLAY_BALL, 'B', Items.BRICK });
		
		
		if (ConfigRFC.craftingUpgrade)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RFCItems.upgrades, 1, 1), "LCL", "CFC", "LCL", 'L', "logWood", 'C', "workbench", 'F', RFCBlocks.blockRFC ));
		if (ConfigRFC.enderUpgrade)
			GameRegistry.addRecipe(new ItemStack(RFCItems.upgrades, 1, 2), new Object[] { "OEO", "EFE", "OEO", 'O', Blocks.OBSIDIAN, 'E', Items.ENDER_EYE, 'F', RFCBlocks.blockRFC });
		if (ConfigRFC.oreDictUpgrade)
			GameRegistry.addRecipe(new ItemStack(RFCItems.upgrades, 1, 3), new Object[] { "GOG", "SFB", "GJG", 'G', Blocks.GLASS, 'O', new ItemStack(Blocks.PLANKS, 1, 0), 'S', new ItemStack(Blocks.PLANKS, 1, 1), 'B', new ItemStack(Blocks.PLANKS, 1, 2), 'J', new ItemStack(Blocks.PLANKS, 1, 3), 'F', RFCBlocks.blockRFC });
		if (ConfigRFC.mobUpgrade) {
			GameRegistry.addRecipe(new ItemStack(RFCItems.upgrades, 1, 4), new Object[] { "IGI", "BFR", "ISI", 'I', Items.IRON_INGOT, 'G', Items.GUNPOWDER, 'B', Items.BONE, 'R', Items.ROTTEN_FLESH, 'S', Items.STRING, 'F', RFCBlocks.blockRFC });
			GameRegistry.addRecipe(new ItemStack(RFCItems.emptyFolder, 4, 2), new Object[] { "PGS", "PBR", "PPP", 'P', Items.PAPER, 'G', Items.GUNPOWDER, 'S', Items.STRING, 'B', Items.BONE, 'R', Items.ROTTEN_FLESH });
		}
		if (ConfigRFC.nametagRecipe)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.NAME_TAG), " PP", " BP", "S  ", 'P', Items.PAPER, 'B', "slimeball", 'S', Items.STRING ));
		
		List<ItemStack> inputs1 = new ArrayList<ItemStack>() {{ add(new ItemStack(RFCItems.emptyFolder, 1, 0)); }};
		List<ItemStack> inputs2 = new ArrayList<ItemStack>() {{ add(new ItemStack(RFCItems.emptyFolder, 1, 1)); }};
		
		GameRegistry.addRecipe(new FolderStorageRecipe(new ItemStack(RFCItems.folder, 1, 0), inputs1));
		GameRegistry.addRecipe(new FolderStorageRecipe(new ItemStack(RFCItems.folder, 1, 2), inputs2));
		GameRegistry.addRecipe(new FolderExtractRecipe());
		GameRegistry.addRecipe(new FolderMergeRecipe());
		GameRegistry.addRecipe(new FolderTapeRecipe());
		
		RecipeSorter.register("FolderExtractRecipe", FolderExtractRecipe.class, Category.SHAPELESS, "");
		RecipeSorter.register("FolderStorageRecipe", FolderStorageRecipe.class, Category.SHAPELESS, "");
		RecipeSorter.register("FolderMergeRecipe", FolderMergeRecipe.class, Category.SHAPELESS, "");
		RecipeSorter.register("FolderTapeRecipe", FolderTapeRecipe.class, Category.SHAPELESS, "");
	}
}
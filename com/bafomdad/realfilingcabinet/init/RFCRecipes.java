package com.bafomdad.realfilingcabinet.init;

import java.util.ArrayList;
import java.util.List;

import scala.actors.threadpool.Arrays;
import vazkii.botania.api.BotaniaAPI;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.crafting.*;
import com.bafomdad.realfilingcabinet.integration.BotaniaRFC;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RFCRecipes {

	public static void init() {
		
		GameRegistry.addRecipe(new ItemStack(RFCItems.emptyFolder, 8, 0), "P  ", "P  ", "PPP", 'P', Items.PAPER );
		GameRegistry.addRecipe(new ItemStack(RFCItems.emptyFolder, 4, 1), "P  ", "PA ", "PPP", 'P', Items.PAPER, 'A', Items.IRON_PICKAXE );
		GameRegistry.addRecipe(new ItemStack(RFCItems.emptyFolder, 4, 3), "PB ", "PWL", "PPP", 'P', Items.PAPER, 'B', Items.BUCKET, 'W', Items.WATER_BUCKET, 'L', Items.LAVA_BUCKET );
		GameRegistry.addRecipe(new ItemStack(RFCItems.emptyFolder, 4, 4), "PB ", "PWE", "PPP", 'P', Items.PAPER, 'B', Items.POTIONITEM, 'W', Items.WRITTEN_BOOK, 'E', Items.ENCHANTED_BOOK );
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RFCBlocks.blockRFC), "III", "ICI", "III", 'I', Blocks.IRON_BARS, 'C', "chest" ));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RFCItems.whiteoutTape), " P ",  "PSP", "BP ", 'P', Items.PAPER, 'S', "slimeball", 'B', new ItemStack(Items.DYE, 1, 15) ));
		GameRegistry.addRecipe(new ItemStack(RFCItems.magnifyingGlass), "G  ", " S ", "   ", 'S', Items.STICK, 'G', Blocks.GLASS_PANE );
		GameRegistry.addRecipe(new ItemStack(RFCItems.filter), " R ", "RAR", " R ", 'R', new ItemStack(Items.DYE, 1, 1), 'A', Items.ARROW );
		
		GameRegistry.addRecipe(new ItemStack(RFCItems.keys, 1, 0), "  N", " N ", "I  ", 'N', Items.GOLD_NUGGET, 'I', Items.GOLD_INGOT );
		GameRegistry.addRecipe(new ItemStack(RFCItems.keys, 1, 1), "  C", " C ", "B  ", 'C', Items.CLAY_BALL, 'B', Items.BRICK );
		
		if (ConfigRFC.craftingUpgrade)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RFCItems.upgrades, 1, 1), "LCL", "CFC", "LCL", 'L', "logWood", 'C', "workbench", 'F', RFCBlocks.blockRFC ));
		if (ConfigRFC.enderUpgrade)
			GameRegistry.addRecipe(new ItemStack(RFCItems.upgrades, 1, 2), "OEO", "EFE", "OEO", 'O', Blocks.OBSIDIAN, 'E', Items.ENDER_EYE, 'F', RFCBlocks.blockRFC );
		if (ConfigRFC.oreDictUpgrade)
			GameRegistry.addRecipe(new ItemStack(RFCItems.upgrades, 1, 3), "GOG", "SFB", "GJG", 'G', Blocks.GLASS, 'O', new ItemStack(Blocks.PLANKS, 1, 0), 'S', new ItemStack(Blocks.PLANKS, 1, 1), 'B', new ItemStack(Blocks.PLANKS, 1, 2), 'J', new ItemStack(Blocks.PLANKS, 1, 3), 'F', RFCBlocks.blockRFC );
		if (ConfigRFC.mobUpgrade) {
			GameRegistry.addRecipe(new ItemStack(RFCItems.upgrades, 1, 4), "IGI", "BFR", "ISI", 'I', Items.IRON_INGOT, 'G', Items.GUNPOWDER, 'B', Items.BONE, 'R', Items.ROTTEN_FLESH, 'S', Items.STRING, 'F', RFCBlocks.blockRFC );
			GameRegistry.addRecipe(new ItemStack(RFCItems.emptyFolder, 4, 2), "PGS", "PBR", "PPP", 'P', Items.PAPER, 'G', Items.GUNPOWDER, 'S', Items.STRING, 'B', Items.BONE, 'R', Items.ROTTEN_FLESH );
		}
		if (ConfigRFC.fluidUpgrade) {
			GameRegistry.addRecipe(new ItemStack(RFCItems.upgrades, 1, 5), "ZBZ", "LFW", "ZBZ", 'Z', Blocks.LAPIS_BLOCK, 'B', Items.BUCKET, 'F', RFCBlocks.blockRFC, 'L', Items.LAVA_BUCKET, 'W', Items.WATER_BUCKET );
		}
		if (ConfigRFC.lifeUpgrade) {
			GameRegistry.addRecipe(new ItemStack(RFCItems.upgrades, 1, 6), "EDE", "DFD", "EDE", 'E', Items.EMERALD, 'D', Items.DIAMOND, 'F', RFCBlocks.blockRFC );
		}
		if (ConfigRFC.nametagRecipe)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.NAME_TAG), " PP", " BP", "S  ", 'P', Items.PAPER, 'B', "slimeball", 'S', Items.STRING ));
		
		if (RealFilingCabinet.botaniaLoaded && ConfigRFC.botaniaIntegration) {
			BotaniaAPI.registerManaInfusionRecipe(new ItemStack(RFCItems.manaFolder), new ItemStack(RFCItems.emptyFolder, 1, 0), 2000);
			GameRegistry.addRecipe(new ItemStack(RFCItems.manaUpgrade), "SMS", "MFM", "SMS", 'S', Blocks.STONE, 'M', new ItemStack(RFCItems.manaFolder), 'F', RFCBlocks.blockRFC );
		}
		List<ItemStack> inputs1 = new ArrayList<ItemStack>() {{ add(new ItemStack(RFCItems.emptyFolder, 1, 0)); }};
		List<ItemStack> inputs2 = new ArrayList<ItemStack>() {{ add(new ItemStack(RFCItems.emptyFolder, 1, 1)); }};
		List<ItemStack> inputs3 = new ArrayList<ItemStack>() {{ add(new ItemStack(RFCItems.emptyFolder, 1, 4)); }};
		
		GameRegistry.addRecipe(new FolderStorageRecipe(new ItemStack(RFCItems.folder, 1, 0), inputs1));
		GameRegistry.addRecipe(new FolderStorageRecipe(new ItemStack(RFCItems.folder, 1, 2), inputs2));
		GameRegistry.addRecipe(new FolderStorageRecipe(new ItemStack(RFCItems.folder, 1, 5), inputs3));
		GameRegistry.addRecipe(new FolderExtractRecipe());
		GameRegistry.addRecipe(new FolderMergeRecipe());
		GameRegistry.addRecipe(new FolderTapeRecipe());
		
		RecipeSorter.register("FolderExtractRecipe", FolderExtractRecipe.class, Category.SHAPELESS, "");
		RecipeSorter.register("FolderStorageRecipe", FolderStorageRecipe.class, Category.SHAPELESS, "");
		RecipeSorter.register("FolderMergeRecipe", FolderMergeRecipe.class, Category.SHAPELESS, "");
		RecipeSorter.register("FolderTapeRecipe", FolderTapeRecipe.class, Category.SHAPELESS, "");
	}
}
package com.bafomdad.realfilingcabinet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

import com.bafomdad.realfilingcabinet.blocks.BlockRFC;
import com.bafomdad.realfilingcabinet.blocks.ItemBlockRFC;
import com.bafomdad.realfilingcabinet.blocks.TileEntityRFC;
import com.bafomdad.realfilingcabinet.core.TabRFC;
import com.bafomdad.realfilingcabinet.crafting.FolderExtractRecipe;
import com.bafomdad.realfilingcabinet.crafting.FolderMergeRecipe;
import com.bafomdad.realfilingcabinet.crafting.FolderStorageRecipe;
import com.bafomdad.realfilingcabinet.crafting.FolderTapeRecipe;
import com.bafomdad.realfilingcabinet.gui.GuiFileList;
import com.bafomdad.realfilingcabinet.integration.AppliedEnergisticsRFC;
import com.bafomdad.realfilingcabinet.integration.AspectsRFC;
import com.bafomdad.realfilingcabinet.integration.WailaRFC;
import com.bafomdad.realfilingcabinet.items.ItemEmptyFolder;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.items.ItemUpgrades;
import com.bafomdad.realfilingcabinet.items.ItemWhiteoutTape;
import com.bafomdad.realfilingcabinet.network.RFCPacketHandler;
import com.bafomdad.realfilingcabinet.proxies.CommonProxy;
import com.bafomdad.realfilingcabinet.storage.OreDictUtils.OreDictRegistry;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "realfilingcabinet", name="Real Filing Cabinet", version="0.4.0", dependencies="after:waila;required-after:Forge@[10.13.4.1557,)")
public class RealFilingCabinet {

	public static final String MOD_ID = "realfilingcabinet";
	@SidedProxy(clientSide="com.bafomdad.realfilingcabinet.proxies.ClientProxy", serverSide="com.bafomdad.realfilingcabinet.proxies.CommonProxy")
	public static CommonProxy proxy;
	@Mod.Instance(MOD_ID)
	public static RealFilingCabinet instance;
	
	public static ConfigRFC config;
	
	public static Item itemFolder;
	public static Item itemEmptyFolder;
	public static Item itemWhiteoutTape;
	public static Item itemMagnifyingGlass;
	public static Item itemUpgrades;
	public static Block blockRFC;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		config = new ConfigRFC();
		config.loadConfig(event);
		
		proxy.init();
		RFCPacketHandler.init();
		
		itemFolder = new ItemFolder().setUnlocalizedName(MOD_ID + "." + "folder");
		GameRegistry.registerItem(itemFolder, "ItemFilingFolder");
		
		itemEmptyFolder = new ItemEmptyFolder().setUnlocalizedName(MOD_ID + "." + "emptyfolder").setTextureName("realfilingcabinet:emptyfolder").setCreativeTab(TabRFC.instance);
		GameRegistry.registerItem(itemEmptyFolder, "ItemEmptyFolder");
		
		itemWhiteoutTape = new ItemWhiteoutTape().setUnlocalizedName(MOD_ID + "." + "whiteouttape").setTextureName("realfilingcabinet:whiteouttape").setCreativeTab(TabRFC.instance);
		GameRegistry.registerItem(itemWhiteoutTape, "ItemWhiteoutTape");
		
		itemMagnifyingGlass = new Item().setUnlocalizedName(MOD_ID + "." + "magnifyingglass").setMaxStackSize(1).setTextureName("realfilingcabinet:magnifying_glass").setCreativeTab(TabRFC.instance);
		GameRegistry.registerItem(itemMagnifyingGlass, "ItemLabelTab");
		
		itemUpgrades = new ItemUpgrades().setCreativeTab(TabRFC.instance);
		GameRegistry.registerItem(itemUpgrades, "ItemUpgrades");
		
		blockRFC = new BlockRFC(Material.iron).setBlockName(MOD_ID + "." + "realfilingcabinet").setBlockTextureName("realfilingcabinet:filingcabinet").setCreativeTab(TabRFC.instance);
		GameRegistry.registerBlock(blockRFC, ItemBlockRFC.class, blockRFC.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityRFC.class, "newTile");
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		
		proxy.registerRenderers();
		OreDictRegistry.instance().init();
		
		GameRegistry.addRecipe(new ItemStack(itemEmptyFolder, 8, 0), new Object[] { "P  ", "P  ", "PPP", 'P', Items.paper });
		GameRegistry.addShapedRecipe(new ItemStack(blockRFC), new Object[] { "III", "ICI", "III", 'I', Blocks.iron_bars, 'C', Blocks.chest });
		GameRegistry.addShapedRecipe(new ItemStack(blockRFC), new Object[] { "III", "ICI", "III", 'I', Blocks.iron_bars, 'C', Blocks.trapped_chest });
		GameRegistry.addRecipe(new ItemStack(itemWhiteoutTape), new Object[] { " P ", "PSP", "BP ", 'P', Items.paper, 'S', Items.slime_ball, 'B', new ItemStack(Items.dye, 1, 15) });
		GameRegistry.addShapelessRecipe(new ItemStack(itemMagnifyingGlass), new Object[] { Items.stick, Blocks.glass_pane });
		
		if (!config.craftingUpgrade)
		{
			GameRegistry.addRecipe(new ItemStack(itemUpgrades, 1, 1), new Object[] { "LCL", "CFC", "LCL", 'L',  Blocks.log, 'C', Blocks.crafting_table, 'F', blockRFC });
			GameRegistry.addRecipe(new ItemStack(itemUpgrades, 1, 1), new Object[] { "LCL", "CFC", "LCL", 'L',  Blocks.log2, 'C', Blocks.crafting_table, 'F', blockRFC });
		}
		if (!config.enderUpgrade)
			GameRegistry.addRecipe(new ItemStack(itemUpgrades, 1, 2), new Object[] { "OEO", "EFE", "OEO", 'O', Blocks.obsidian, 'E', Items.ender_eye, 'F', blockRFC });
		if (!config.oreDictUpgrade)
			GameRegistry.addRecipe(new ItemStack(itemUpgrades, 1, 3), new Object[] { "GOG", "SFB", "GJG", 'G', Blocks.glass, 'O', new ItemStack(Blocks.planks, 1, 0), 'S', new ItemStack(Blocks.planks, 1, 1), 'B', new ItemStack(Blocks.planks, 1, 2), 'J', new ItemStack(Blocks.planks, 1, 3), 'F', blockRFC });
		
		GameRegistry.addRecipe(new FolderExtractRecipe());
		GameRegistry.addRecipe(new FolderMergeRecipe());
		GameRegistry.addRecipe(new FolderTapeRecipe());
		GameRegistry.addRecipe(new FolderStorageRecipe());
		
		RecipeSorter.register("FolderExtractRecipe", FolderExtractRecipe.class, Category.SHAPELESS, "");
		RecipeSorter.register("FolderMergeRecipe", FolderMergeRecipe.class, Category.SHAPELESS, "");
		RecipeSorter.register("FolderTapeRecipe", FolderTapeRecipe.class, Category.SHAPELESS, "");
		RecipeSorter.register("FolderStorageRecipe", FolderStorageRecipe.class, Category.SHAPELESS, "");
		
		if (Loader.isModLoaded("Thaumcraft"))
			AspectsRFC.register();
		if (Loader.isModLoaded("Waila"))
			WailaRFC.register();
		if (Loader.isModLoaded("appliedenergistics2"))
			try {
				AppliedEnergisticsRFC.register();
			} catch (Throwable e) {
				e.printStackTrace();
			}
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
		AppliedEnergisticsRFC.postInit();
		MinecraftForge.EVENT_BUS.register(new GuiFileList(Minecraft.getMinecraft()));
	}
}

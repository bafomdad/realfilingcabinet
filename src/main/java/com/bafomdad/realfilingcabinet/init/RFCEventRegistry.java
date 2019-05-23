package com.bafomdad.realfilingcabinet.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.botania.api.BotaniaAPI;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.api.RealFilingCabinetAPI;
import com.bafomdad.realfilingcabinet.blocks.*;
import com.bafomdad.realfilingcabinet.blocks.tiles.*;
import com.bafomdad.realfilingcabinet.crafting.*;
import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.helpers.enums.MobUpgradeType;
import com.bafomdad.realfilingcabinet.helpers.enums.UpgradeType;
import com.bafomdad.realfilingcabinet.items.*;
import com.bafomdad.realfilingcabinet.items.itemblocks.*;

@Mod.EventBusSubscriber(modid=RealFilingCabinet.MOD_ID)
public class RFCEventRegistry {
	
	public static List<Block> blocks = new ArrayList();
	public static List<Item> items = new ArrayList();
	public static int entityID;
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		
		event.getRegistry().registerAll(
				registerBlock(new BlockFilingCabinet(), "modelcabinet").setHardness(5.0F).setResistance(1000.0F)
				);
		GameRegistry.registerTileEntity(TileFilingCabinet.class, new ResourceLocation(RealFilingCabinet.MOD_ID, "filingcabinet"));
		
		RFCIntegration.canLoad(RFCIntegration.BOTANIA).ifPresent(b -> b.registerBlocks(event));
		RFCIntegration.canLoad(RFCIntegration.THAUMCRAFT).ifPresent(b -> b.registerBlocks(event));
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		
		event.getRegistry().registerAll(
				registerItem(new ItemEmptyFolder(), "emptyfolder").setMaxStackSize(8).setHasSubtypes(true).setMaxDamage(0),
				registerItem(new ItemFolder(), "folder", false).setMaxStackSize(1).setHasSubtypes(true),
				registerItem(new ItemUpgrades(), "upgrade").setMaxStackSize(16).setHasSubtypes(true).setMaxDamage(0),
				registerItem(new ItemMagnifyingGlass(), "magnifyingglass").setMaxStackSize(1),
				registerItem(new ItemWhiteoutTape(), "whiteouttape").setMaxStackSize(1).setMaxDamage(25),
				registerItem(new ItemMysteryFolder(), "mysteryfolder").setMaxStackSize(1),
				registerItem(new ItemSuitcase(), "suitcase").setMaxStackSize(1),
				registerItem(new ItemEmptyDyedFolder(), "emptydyedfolder").setMaxStackSize(8).setHasSubtypes(true).setMaxDamage(0),
				registerItem(new ItemDyedFolder(), "dyedfolder", false).setMaxStackSize(1).setHasSubtypes(true),
				registerItem(new ItemAutoFolder(), "autofolder").setMaxStackSize(1).setHasSubtypes(true),
				registerItem(new Item(), "debugger").setMaxStackSize(1),
				registerItem(new Item(), "filter").setMaxStackSize(16),
				registerItem(new ItemKeys(), "key").setMaxStackSize(1).setHasSubtypes(true).setMaxDamage(0),
				registerItem(new ItemBlockRFC(RFCBlocks.MODELCABINET), "modelcabinet")
				);
		RFCIntegration.canLoad(RFCIntegration.BOTANIA).ifPresent(i -> i.registerItems(event));
		RFCIntegration.canLoad(RFCIntegration.THAUMCRAFT).ifPresent(i -> i.registerItems(event));
	}
	
	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		
		event.getRegistry().registerAll(
				new FolderStorageRecipe().setRegistryName(new ResourceLocation(RealFilingCabinet.MOD_ID, "storagefolder")),
				new FolderExtractRecipe().setRegistryName(new ResourceLocation(RealFilingCabinet.MOD_ID, "folderextract")),
				new FolderTapeRecipe().setRegistryName(new ResourceLocation(RealFilingCabinet.MOD_ID, "foldertape")),
				new FolderMergeRecipe().setRegistryName(new ResourceLocation(RealFilingCabinet.MOD_ID, "foldermerge"))
				);
		RFCIntegration.canLoad(RFCIntegration.BOTANIA).ifPresent(r -> r.registerRecipes(event));
		RFCIntegration.canLoad(RFCIntegration.THAUMCRAFT).ifPresent(r -> r.registerRecipes(event));
		Arrays.stream(UpgradeType.values()).forEach(u -> RealFilingCabinetAPI.registerUpgrade(new ItemStack(RFCItems.UPGRADE, 1, u.ordinal()), u.getTexture(), u.getTag()));
		Arrays.stream(MobUpgradeType.values()).forEach(mu -> RealFilingCabinetAPI.registerMobUpgrade(new ItemStack(RFCItems.UPGRADE, 1, mu.getItemDamage()), mu.getModel(), mu.getTexture(), mu.getTag()));
	}
	
	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		
		event.getRegistry().register(registerEntity("entitycabinet", EntityCabinet.class));
	}
	
	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		
		event.getRegistry().registerAll(
				RFCSounds.SQUEAK,
				RFCSounds.DRAWER
				);
	}
	
	public static <T extends Block> T registerBlock(T newBlock, String name) {
		
		newBlock.setRegistryName(name);
		newBlock.setTranslationKey(RealFilingCabinet.MOD_ID + "." + name);
		newBlock.setCreativeTab(TabRFC.instance);
		blocks.add(newBlock);

		return newBlock;
	}
	
	public static <T extends Item> T registerItem(T newItem, String name) {
		
		return registerItem(newItem, name, true);
	}
	
	public static <T extends Item> T registerItem(T newItem, String name, boolean creativeTab) {
		
		newItem.setRegistryName(name);
		newItem.setTranslationKey(RealFilingCabinet.MOD_ID + "." + name);
		if (creativeTab)
			newItem.setCreativeTab(TabRFC.instance);
		items.add(newItem);
		
		return newItem;
	}
	
	public static EntityEntry registerEntity(String entityName, Class entity) {
		
		EntityEntryBuilder builder = EntityEntryBuilder.create();
		builder.name(RealFilingCabinet.MOD_ID + ":" +  entityName);
		builder.id(new ResourceLocation(RealFilingCabinet.MOD_ID, entityName), entityID++);
		builder.tracker(64, 1, true);
		builder.entity(entity);
		
		return builder.build();
	}
}

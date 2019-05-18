package com.bafomdad.realfilingcabinet.init;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(RealFilingCabinet.MOD_ID)
public class RFCItems {

	public static final Item EMPTYFOLDER = Items.AIR;
	public static final Item FOLDER = Items.AIR;
	public static final Item MAGNIFYINGGLASS = Items.AIR;
	public static final Item WHITEOUTTAPE = Items.AIR;
	public static final Item UPGRADE = Items.AIR;
	public static final Item MYSTERYFOLDER = Items.AIR;
	public static final Item SUITCASE = Items.AIR;
	public static final Item EMPTYDYEDFOLDER = Items.AIR;
	public static final Item DYEDFOLDER = Items.AIR;
	public static final Item FILTER = Items.AIR;
	public static final Item KEY = Items.AIR;
	public static final Item DEBUGGER = Items.AIR;
	public static final Item AUTOFOLDER = Items.AIR;
	
	// COMPAT ITEMS
	public static final Item FOLDER_ASPECT = Items.AIR;
	public static final Item FOLDER_MANA = Items.AIR;
	
	@ObjectHolder("thaumcraft:phial")
	public static final Item PHIAL = Items.AIR;
}

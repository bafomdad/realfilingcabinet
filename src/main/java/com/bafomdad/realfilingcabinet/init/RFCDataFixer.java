package com.bafomdad.realfilingcabinet.init;

import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixableData;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.google.common.collect.ImmutableMap;

public class RFCDataFixer {

	public static final int DATA_FIXER_VERSION = 1;
	
	public static void init() {
		
		ModFixs fixes = FMLCommonHandler.instance().getDataFixer().init(RealFilingCabinet.MOD_ID, DATA_FIXER_VERSION);
		fixes.registerFix(FixTypes.BLOCK_ENTITY, new RFCTileFixer());

		CompoundDataFixer fixer = FMLCommonHandler.instance().getDataFixer();
		fixer.registerWalker(FixTypes.BLOCK_ENTITY, new ItemStackDataLists(TileEntityRFC.class, "inventory"));
	}
	
	private static class RFCTileFixer implements IFixableData {

		private final Map<String, String> teNames;
		
		{
			ImmutableMap.Builder<String, String> nameMap = ImmutableMap.builder();
			nameMap
				.put("minecraft:tileaspectcabinet", 					"realfilingcabinet:aspectcabinet")
				.put("minecraft:tilemanacabinet",       				"realfilingcabinet:manacabinet")
				.put("minecraft:realfilingcabinet_tilefilingcabinet", 	"realfilingcabinet:filingcabinet");
			teNames = nameMap.build();
		}
		
		@Override
		public int getFixVersion() {

			return DATA_FIXER_VERSION;
		}

		@Override
		public NBTTagCompound fixTagCompound(NBTTagCompound tag) {

			String tileLocation = tag.getString("id");
			tag.setString("id", teNames.getOrDefault(tileLocation, tileLocation));
			
			return tag;
		}
	}
}

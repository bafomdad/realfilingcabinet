package com.bafomdad.realfilingcabinet.init;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;

@ObjectHolder(RealFilingCabinet.MOD_ID)
public class RFCBlocks {

	public static final Block MODELCABINET = Blocks.AIR;
	
	// COMPAT BLOCKS
	public static final Block MANACABINET = Blocks.AIR;
	public static final Block ASPECTCABINET = Blocks.AIR;
}

package com.bafomdad.realfilingcabinet.network;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VanillaPacketDispatcher {

	public static void dispatchTEToNearbyPlayers(TileEntity tile) {
		
		IBlockState state = tile.getWorld().getBlockState(tile.getPos());
		tile.getWorld().notifyBlockUpdate(tile.getPos(), state, state, 8);
	}
	
	public static void dispatchTEToNearbyPlayers(World world, BlockPos pos) {
		
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null)
			dispatchTEToNearbyPlayers(tile);
	}
}

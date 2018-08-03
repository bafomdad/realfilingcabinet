package com.bafomdad.realfilingcabinet.network;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class VanillaPacketDispatcher {

	public static void dispatchTEToNearbyPlayers(TileEntity tile) {
		
		IBlockState state = tile.getWorld().getBlockState(tile.getPos());
		tile.getWorld().notifyBlockUpdate(tile.getPos(), state, state, 8);
		if (tile.getWorld().isRemote)
			tile.getWorld().markBlockRangeForRenderUpdate(tile.getPos(), tile.getPos());
	}
	
	public static void dispatchTEToNearbyPlayers(World world, BlockPos pos) {
		
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null)
			dispatchTEToNearbyPlayers(tile);
	}
	
	public static void sendToNearbyPlayers(World world, BlockPos pos, IMessage toSend) {
		
		if (world instanceof WorldServer) {
			WorldServer ws = ((WorldServer)world);
			
			for (EntityPlayer player : ws.playerEntities) {
				EntityPlayerMP ep = ((EntityPlayerMP)player);
				
				if (ep.getDistanceSq(pos) < 64 * 64 && ws.getPlayerChunkMap().isPlayerWatchingChunk(ep, pos.getX() >> 4, pos.getZ() >> 4))
					RFCPacketHandler.INSTANCE.sendTo(toSend, ep);
			}
		}
	}
}

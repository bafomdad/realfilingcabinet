package com.bafomdad.realfilingcabinet.blocks.tiles;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.BlockRFC;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TileFilingCabinet extends TileEntity {
	
	public float offset, renderOffset;
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		
		super.writeToNBT(tag);
		writeCustomNBT(tag);
		return tag;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		
		super.readFromNBT(tag);
		readCustomNBT(tag);
	}
	
	public void writeCustomNBT(NBTTagCompound tag) {}
	
	public void readCustomNBT(NBTTagCompound tag) {}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		
		if (packet != null && packet.getNbtCompound() != null)
			readCustomNBT(packet.getNbtCompound());
		
		markBlockForRenderUpdate();
	}
	
	public void markBlockForUpdate() {
		
		IBlockState state = getWorld().getBlockState(pos);
		getWorld().notifyBlockUpdate(pos, state, state, 3);
	}
	
	public void markBlockForRenderUpdate() {
		
		getWorld().markBlockRangeForRenderUpdate(pos, pos);
	}
	
	@Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
    	
    	return oldState.getBlock() != newState.getBlock();
    }
}

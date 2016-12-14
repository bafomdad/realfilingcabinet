package com.bafomdad.realfilingcabinet.api;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IFilingCabinet extends ITileEntityProvider {

	public void leftClick(TileEntity tile, EntityPlayer player);
	
	public void rightClick(TileEntity tile, EntityPlayer player, EnumFacing side, float hitX, float hity, float hitZ);
}

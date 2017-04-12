package com.bafomdad.realfilingcabinet.gui;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandlerRFC implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityRFC)
			return new ContainerRFC(player, (TileEntityRFC)tile);
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityRFC)
			return new GuiContainerRFC(new ContainerRFC(player, (TileEntityRFC)tile));

		return null;
	}
}

package com.bafomdad.realfilingcabinet.api;

import java.util.List;

import net.minecraft.tileentity.TileEntity;

public interface IBlockCabinet {
	
	public List<String> getInfoOverlay(TileEntity tile, boolean crouching);
}

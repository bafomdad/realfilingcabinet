package com.bafomdad.realfilingcabinet.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public interface IUpgrade {

	public boolean canApply(ItemStack upgrade, EntityPlayer player);
	
	public default void tickUpgrade(ItemStack upgrade, TileEntity tile) {}
}

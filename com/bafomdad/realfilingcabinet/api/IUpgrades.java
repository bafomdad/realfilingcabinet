package com.bafomdad.realfilingcabinet.api;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public interface IUpgrades {

	public boolean canApply(TileEntity tile, ItemStack upgrade);
}

package com.bafomdad.realfilingcabinet.api;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IUpgrades {

	public boolean canApply(TileEntityRFC tile, ItemStack upgrade, EntityPlayer player);
}

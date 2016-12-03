package com.bafomdad.realfilingcabinet.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;

public interface IUpgrades {

	public boolean canApply(TileEntityRFC tile, ItemStack upgrade, EntityPlayer player);
}

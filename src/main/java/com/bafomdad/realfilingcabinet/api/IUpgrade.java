package com.bafomdad.realfilingcabinet.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IUpgrade {

	public boolean canApply(ItemStack upgrade, EntityPlayer player);
}

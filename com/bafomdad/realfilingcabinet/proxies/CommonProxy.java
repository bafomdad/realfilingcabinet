package com.bafomdad.realfilingcabinet.proxies;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class CommonProxy {

	public void init() {
		
	}
	
	public void registerRenderers() {
		
	}
	
	public void updatePlayerInventory(EntityPlayer player) {
		
		if (player instanceof EntityPlayerMP)
			((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
	}
	
	public World getClientWorld() {
		
		return null;
	}
}

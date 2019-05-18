package com.bafomdad.realfilingcabinet.api;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

public interface ILockableCabinet extends ITileCabinet {

	public UUID getOwner();
	
	public boolean setOwner(UUID owner);
	
	public boolean isCabinetLocked();
	
	public boolean hasKeyCopy(EntityPlayer player, UUID uuid);
}

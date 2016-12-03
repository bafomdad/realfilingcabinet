package com.bafomdad.realfilingcabinet.api;

import java.util.UUID;

public interface ILockableCabinet {

	public UUID getCabinetOwner();
	
	public boolean setOwner(UUID owner);
	
	public boolean isCabinetLocked();
}

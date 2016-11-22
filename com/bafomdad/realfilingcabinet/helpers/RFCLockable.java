package com.bafomdad.realfilingcabinet.helpers;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.LockCode;

public class RFCLockable extends TileFilingCabinet {

	private LockCode code = LockCode.EMPTY_CODE;
	
	@Override
	public void readCustomNBT(NBTTagCompound tag) {
		
		code = LockCode.fromNBT(tag);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound tag) {
		
		if (code != null)
			code.toNBT(tag);
	}
	
	public boolean isLocked() {
		
		return this.code != null && !this.code.isEmpty();
	}
	
	public LockCode getLockCode() {
		
		return this.code;
	}
	
	public void setLockCode(LockCode code) {
		
		this.code = code;
	}
}

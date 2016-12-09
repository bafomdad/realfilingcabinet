package com.bafomdad.realfilingcabinet.entity.ai;

import com.bafomdad.realfilingcabinet.entity.EntityCabinet;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public abstract class EntityAIBaseRFC extends EntityAIBase {

	protected EntityCabinet cabinet;
	private double range = 10;
	
	public boolean isWithinBoundaries() {
		
		if (cabinet.getOwnerId() != null)
			return true;
		
		BlockPos pos = cabinet.getOriginPoint();
		AxisAlignedBB aabb = new AxisAlignedBB(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).expand(range, range, range);
		
		return aabb.intersects(cabinet.posX - 1, cabinet.posY - 1, cabinet.posZ - 1, cabinet.posX + 1, cabinet.posY + 1, cabinet.posZ + 1);
	}
	
	public double getRange() {
		
		return this.range;
	}
	
	public void setRange(double range) {
		
		this.range = range;
	}
}

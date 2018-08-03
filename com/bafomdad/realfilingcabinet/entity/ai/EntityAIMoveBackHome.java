package com.bafomdad.realfilingcabinet.entity.ai;

import com.bafomdad.realfilingcabinet.entity.EntityCabinet;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;

public class EntityAIMoveBackHome extends EntityAIBase {
	
	private EntityCabinet cabinet;
	private PathNavigate pathFinder;
	private BlockPos dest = BlockPos.ORIGIN;
	
	public EntityAIMoveBackHome(EntityCabinet cabinet) {
		
		this.cabinet = cabinet;
		this.pathFinder = cabinet.getNavigator();
		setMutexBits(5);
	}

	@Override
	public boolean shouldExecute() {

		if (cabinet.homePos == -1 || (this.cabinet.ticksExisted % 20 > 0))
			return false;
		
		if (cabinet.world != null) {
			BlockPos pos = BlockPos.fromLong(cabinet.homePos);
			if (cabinet.getDistance(pos.getX(), pos.getY(), pos.getZ()) > 5.0D)
			{
				dest = pos;
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void startExecuting() {
		
		if (dest != BlockPos.ORIGIN)
			pathFinder.tryMoveToXYZ(dest.getX(), dest.getY(), dest.getZ(), 0.6F);
	}
	
	@Override
	public boolean shouldContinueExecuting() {
		
		return cabinet.isEntityAlive() && cabinet.getDistance(dest.getX(), dest.getY(), dest.getZ()) > 1.0D;
	}
}

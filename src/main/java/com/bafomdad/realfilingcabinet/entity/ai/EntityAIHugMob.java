package com.bafomdad.realfilingcabinet.entity.ai;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.helpers.MobUpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;

public class EntityAIHugMob extends EntityAIBase {
	
	private final EntityCabinet cabinet;
	private final PathNavigate pathFinder;
	private EntityLivingBase targetMob;
	
	public EntityAIHugMob(EntityCabinet cabinet) {
		
		this.cabinet = cabinet;
		this.pathFinder = cabinet.getNavigator();
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		
		if (!pathFinder.noPath() || MobUpgradeHelper.getUpgrade(cabinet, StringLibs.TAG_MOB).isEmpty()) return false;
		
		if (cabinet.world != null) {
			List<EntityLivingBase> mobs = cabinet.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(cabinet.posX - 1, cabinet.posZ - 1, cabinet.posZ - 1, cabinet.posX + 1, cabinet.posY + 1, cabinet.posZ + 1).expand(10.0, 10.0, 10.0));
			double closestDistance = 50.0D;
			for (EntityLivingBase mob : mobs) {
				if (!mob.isDead && mob.onGround && !(mob instanceof EntityPlayer)) {
					double dist = mob.getDistance(cabinet);
					if (dist < closestDistance && cabinet.insert(mob, true) != null) {
						BlockPos pos = BlockPos.fromLong(cabinet.homePos);
						if (cabinet.hasHome() && cabinet.getDistance(pos.getX(), pos.getY(), pos.getZ()) > 10.0D)
							return false;
						
						targetMob = mob;
						cabinet.setYay(true);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public void resetTask() {
		
		pathFinder.clearPath();
		targetMob = null;
		cabinet.setYay(false);
	}
	
	@Override
	public boolean shouldContinueExecuting() {
		
		return cabinet.isEntityAlive() && !pathFinder.noPath() && !targetMob.isDead && !MobUpgradeHelper.hasUpgrade(cabinet);
	}
	
	@Override
	public void startExecuting() {
		
		if (targetMob != null)
			pathFinder.tryMoveToXYZ(targetMob.posX, targetMob.posY, targetMob.posZ, 0.6F);
	}
	
	@Override
	public void updateTask() {
		
		super.updateTask();
		if (!cabinet.world.isRemote) {
			if (targetMob != null && cabinet.getDistance(targetMob) < 1.0) {
				for (int i = 0; i < cabinet.getInventory().getSlots(); i++) {
					if (cabinet.insert(targetMob, false) != null) {
						cabinet.setYay(false);
					}
				}
			}
		}
	}
}

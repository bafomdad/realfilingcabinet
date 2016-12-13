package com.bafomdad.realfilingcabinet.entity.ai;

import java.util.List;

import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.helpers.MobUpgradeHelper;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.utils.MobUtils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class EntityAIHugMob extends EntityAIBase {
	
	private EntityCabinet cabinet;
	private PathNavigate pathFinder;
	private EntityLivingBase targetMob;
	
	public EntityAIHugMob(EntityCabinet cabinet) {
		
		this.cabinet = cabinet;
		this.pathFinder = cabinet.getNavigator();
		setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {

		if (!pathFinder.noPath() || MobUpgradeHelper.getMobUpgrade(cabinet, StringLibs.TAG_MOB) == null)
			return false;
		
		if (cabinet.worldObj != null) {
			List<EntityLivingBase> mobs = cabinet.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(cabinet.posX - 1, cabinet.posY - 1, cabinet.posZ - 1, cabinet.posX + 1, cabinet.posY + 1, cabinet.posZ + 1).expand(10.0, 10.0, 10.0));
			EntityLivingBase closest = null;
			double closestDistance = Double.MAX_VALUE;
			for (EntityLivingBase mob : mobs) {
				if (!mob.isDead && mob.onGround) {
					double dist = mob.getDistanceToEntity(cabinet);
					if (dist < closestDistance && cabinet.getInventory().canInsertMob(mob, false)) {
						BlockPos pos = BlockPos.fromLong(cabinet.homePos);
						if (cabinet.hasHome() && cabinet.getDistance(pos.getX(), pos.getY(), pos.getZ()) > 10.0D)
							return false;
						
						closest = mob;
						closestDistance = dist;
					}
				}
			}
			if (closest != null) {
				targetMob = closest;
				cabinet.setYay(true);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void resetTask() {
		
		pathFinder.clearPathEntity();
		targetMob = null;
		cabinet.setYay(false);
	}
	
	@Override
	public boolean continueExecuting() {
		
		return cabinet.isEntityAlive() && !pathFinder.noPath() && !targetMob.isDead;
	}
	
	@Override
	public void startExecuting() {
		
		if (targetMob != null)
			pathFinder.tryMoveToXYZ(targetMob.posX, targetMob.posY, targetMob.posZ, 0.6F);
	}
	
	@Override
	public void updateTask() {
		
		super.updateTask();
		if (!cabinet.worldObj.isRemote) {
			if (targetMob != null && cabinet.getDistanceToEntity(targetMob) < 1.5) {
				boolean flag = cabinet.getInventory().canInsertMob(targetMob, true);
				if (flag)
				{
					MobUtils.dropMobEquips(cabinet.worldObj, targetMob);
					targetMob.setDead();
					cabinet.setYay(false);
				}
			}
		}
	}
}

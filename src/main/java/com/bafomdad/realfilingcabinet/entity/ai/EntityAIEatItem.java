package com.bafomdad.realfilingcabinet.entity.ai;

import java.util.List;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import com.bafomdad.realfilingcabinet.entity.EntityCabinet;
import com.bafomdad.realfilingcabinet.helpers.MobUpgradeHelper;

public class EntityAIEatItem extends EntityAIBase {
	
	private final EntityCabinet cabinet;
	private final PathNavigate pathFinder;
	private EntityItem targetItem = null;
	
	public EntityAIEatItem(EntityCabinet cabinet) {
		
		this.cabinet = cabinet;
		this.pathFinder = cabinet.getNavigator();
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		
		if (!pathFinder.noPath()) return false;
		
		if (cabinet.world != null) {
			List<EntityItem> items = cabinet.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(cabinet.posX - 1, cabinet.posZ - 1, cabinet.posZ - 1, cabinet.posX + 1, cabinet.posY + 1, cabinet.posZ + 1).expand(10.0, 10.0, 10.0));
			double closestDistance = 50.0D;
			for (EntityItem item : items) {
				if (!item.isDead && item.onGround) {
					double dist = item.getDistance(cabinet);
					if (dist < closestDistance && cabinet.insert(item.getItem(), true) != null) {
						BlockPos pos = BlockPos.fromLong(cabinet.homePos);
						if (cabinet.hasHome() && cabinet.getDistance(pos.getX(), pos.getY(), pos.getZ()) > 10.0D)
							return false;
						
						targetItem = item;
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
		targetItem = null;
		cabinet.setYay(false);
	}
	
	@Override
	public boolean shouldContinueExecuting() {

		return cabinet.isEntityAlive() && !pathFinder.noPath() && !targetItem.isDead && !MobUpgradeHelper.hasUpgrade(cabinet);
	}
	
	@Override
	public void startExecuting() {
		
		if (targetItem != null)
			pathFinder.tryMoveToXYZ(targetItem.posX, targetItem.posY, targetItem.posZ, 0.6F);
	}
	
	@Override
	public void updateTask() {
		
		super.updateTask();
		if (!cabinet.world.isRemote) {
			if (targetItem != null && cabinet.getDistance(targetItem) < 1.0) {
				for (int i = 0; i < cabinet.getInventory().getSlots(); i++) {
					if (cabinet.insert(targetItem.getItem(), false) != null) {
						if (targetItem.getItem().getCount() == 0)
							targetItem.setDead();
						cabinet.setYay(false);
					}
				}
			}
		}
	}
}

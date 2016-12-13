package com.bafomdad.realfilingcabinet.entity.ai;

import java.util.List;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import com.bafomdad.realfilingcabinet.entity.EntityCabinet;

public class EntityAIEatItem extends EntityAIBase {
	
	private EntityCabinet cabinet;
	private PathNavigate pathFinder;
	private EntityItem targetItem = null;
	
	public EntityAIEatItem(EntityCabinet cabinet) {
		
		this.cabinet = cabinet;
		this.pathFinder = cabinet.getNavigator();
		setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {

		if (!pathFinder.noPath())
			return false;
			
		if (cabinet.worldObj != null) {
			List<EntityItem> items = cabinet.worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(cabinet.posX - 1, cabinet.posY - 1, cabinet.posZ - 1, cabinet.posX + 1, cabinet.posY + 1, cabinet.posZ + 1).expand(10.0, 10.0, 10.0));
			EntityItem closest = null;
			double closestDistance = Double.MAX_VALUE;
			for (EntityItem item : items) {
				if (!item.isDead && item.onGround) {
					double dist = item.getDistanceToEntity(cabinet);
					if (dist < closestDistance && cabinet.getInventory().canInsertItem(item.getEntityItem())) {
						BlockPos pos = BlockPos.fromLong(cabinet.homePos);
						if (cabinet.hasHome() && cabinet.getDistance(pos.getX(), pos.getY(), pos.getZ()) > 10.0D)
							return false;
						
						closest = item;
						closestDistance = dist;
					}
				}
			}
			if (closest != null) {
				targetItem = closest;
				cabinet.setYay(true);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void resetTask() {
		
		pathFinder.clearPathEntity();
		targetItem = null;
		cabinet.setYay(false);
	}
	
	@Override
	public boolean continueExecuting() {
		
		return cabinet.isEntityAlive() && !pathFinder.noPath() && !targetItem.isDead;
	}
	
	@Override
	public void startExecuting() {
		
		if (targetItem != null)
			pathFinder.tryMoveToXYZ(targetItem.posX, targetItem.posY, targetItem.posZ, 0.6F);
	}
	
	@Override
	public void updateTask() {
		
		super.updateTask();
		if (!cabinet.worldObj.isRemote) {
			if (targetItem != null && cabinet.getDistanceToEntity(targetItem) < 1.0) {
				ItemStack stack = cabinet.getInventory().insertItem(0, targetItem.getEntityItem(), true);
				if (stack == null) {
					cabinet.getInventory().insertItem(0, targetItem.getEntityItem(), false);
					targetItem.setDead();
					cabinet.setYay(false);
				}
			}
		}
	}
}

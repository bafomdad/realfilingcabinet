package com.bafomdad.realfilingcabinet.blocks.tiles;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;

import com.bafomdad.realfilingcabinet.utils.ManaStorageUtils;
import com.google.common.base.Predicates;

import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;

public class TileManaCabinet extends TileEntityRFC implements IManaReceiver, ISparkAttachable {

	private static final long MAX_MANA_INTERNAL = 1000000000 * 8;
	
	// Botania stuff start
	public long getTotalInternalManaPool() {
		
		long total = 0;
		for (int i = 0; i < this.getInventory().getSlots(); i++) {
			ItemStack stack = this.getInventory().getFolder(i);
			if (!stack.isEmpty() && stack.getItem() instanceof IManaItem)
				total += ManaStorageUtils.getManaSize(stack);
		}
		return total;
	}
	
	public int getManaFromFolder() {
		
		for (int i = 0; i < this.getInventory().getSlots(); i++) {
			ItemStack stack = this.getInventory().getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() instanceof IManaItem) {
				int manaSize = ManaStorageUtils.getManaSize(stack);
				if (manaSize >= 0)
					return manaSize;
			}
		}
		return -1;
	}
	
	public void addManaToFolder(int mana) {
		
		for (int i = 0; i < this.getInventory().getSlots(); i++){
			ItemStack stack = this.getInventory().getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() instanceof IManaItem) {
				if (mana > 0 && ManaStorageUtils.isManaFolderFull(stack))
					continue;
				
				if (mana < 0 && ManaStorageUtils.getManaSize(stack) <= 0)
					continue;
				
				ManaStorageUtils.addManaToFolder(stack, mana);
				break;
			}
		}
	}
	
	@Override
	public boolean canRecieveManaFromBursts() {

		return getManaFromFolder() != -1;
	}

	@Override
	public boolean isFull() {

		return getTotalInternalManaPool() == MAX_MANA_INTERNAL || getManaFromFolder() == -1;
	}

	@Override
	public void recieveMana(int mana) {

		int manaToAdd = Math.min(ManaStorageUtils.getMaxManaFolder(), mana);
		this.addManaToFolder(manaToAdd);
	}

	@Override
	public int getCurrentMana() {

		return getManaFromFolder();
	}

	@Override
	public boolean areIncomingTranfersDone() {

		return false;
	}

	@Override
	public void attachSpark(ISparkEntity arg0) {}

	@Override
	public boolean canAttachSpark(ItemStack arg0) {

		return true;
	}

	@Override
	public ISparkEntity getAttachedSpark() {

		List sparks = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.up(), pos.up().add(1, 1, 1)), Predicates.instanceOf(ISparkEntity.class));
		if (sparks.size() == 1) {
			Entity e = (Entity)sparks.get(0);
			return (ISparkEntity)e;
		}
		return null;
	}

	@Override
	public int getAvailableSpaceForMana() {

		if (getTotalInternalManaPool() == this.MAX_MANA_INTERNAL)
			return 0;
		
		return 1000;
	}
}

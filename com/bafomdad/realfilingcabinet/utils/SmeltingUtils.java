package com.bafomdad.realfilingcabinet.utils;

import java.text.NumberFormat;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

public class SmeltingUtils {
	
	public static final int FUEL_TIME = 1600;
	public static final int SMELT_TIME = 100;
	public static final String TAG_SMELTLIST = "RFC_smeltList";
	public static final String TAG_SMELTJOB = "RFC_smeltingJob";
	public static final String TAG_FUELTIME = "RFC_fuelTime";
	
	public static boolean canSmelt(TileEntityRFC tile) {
		
		return UpgradeHelper.getUpgrade(tile, StringLibs.TAG_SMELT) != null;
	}

	public static void createSmeltingJob(TileEntityRFC tile) {
		
		int toSmelt = -1;
		int smeltResult = -1;
		for (int i = 0; i < tile.getInventory().getSlots(); i++) {
			if (hasSmeltingJob(tile, i)) continue;
			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(tile.getInventory().getStackInSlot(i));
			if (!result.isEmpty()) {
				toSmelt = i;
				for (int j = 0; j < tile.getInventory().getSlots(); j++) {
					ItemStack loopStack = tile.getInventory().getStackFromFolder(j);
					if (!loopStack.isEmpty() && loopStack.areItemsEqual(loopStack, result) && toSmelt != j) {
						smeltResult = j;
						break;
					}
				}
			}
		}
		if (toSmelt >= 0 && smeltResult >= 0) {
			addSmeltingJob(tile, toSmelt, smeltResult);
		}
		if (!tile.smeltingJobs.isEmpty() && tile.fuelTime <= 0) {
			for (int k = 0; k < tile.getInventory().getSlots(); k++) {
				ItemStack fuel = tile.getInventory().getStackInSlot(k);
				if (!fuel.isEmpty() && fuel.getItem() == Items.COAL) {
					ItemFolder.remove(tile.getInventory().getTrueStackInSlot(k), 1);
					tile.fuelTime = FUEL_TIME;
					break;
				}
			}
		}
	}
	
	public static void writeSmeltNBT(TileEntityRFC tile, NBTTagCompound tag) {

		if (!canSmelt(tile)) {
			if (!tile.smeltingJobs.isEmpty())
				tile.smeltingJobs.clear();
			tile.fuelTime = 0;
			return;
		}
		tag.setInteger(TAG_FUELTIME, tile.fuelTime);
		NBTTagList tagList = new NBTTagList();
		for (int[] job : tile.smeltingJobs) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setIntArray(TAG_SMELTJOB, job);
			tagList.appendTag(nbt);
		}
		tag.setTag(TAG_SMELTLIST, tagList);
	}
	
	public static void readSmeltNBT(TileEntityRFC tile, NBTTagCompound tag) {
	
//		if (!canSmelt(tile)) {
//			tag.removeTag(TAG_SMELTJOB);
//			return;
//		}
		if (tag.hasKey(TAG_SMELTLIST)) {
//			if (!tile.smeltingJobs.isEmpty()) tile.smeltingJobs.clear();
			
			NBTTagList tagList = tag.getTagList(TAG_SMELTLIST, 11);
			for (int i = 0; i < tagList.tagCount(); i++) {
				int[] job = tagList.getIntArrayAt(i);
				tile.smeltingJobs.add(job);
			}
		}
		tile.fuelTime = tag.getInteger(TAG_FUELTIME);
	}
	
	private static void addSmeltingJob(TileEntityRFC tile, int toSmelt, int smeltResult) {
		
		if (tile.getWorld().isRemote) return;
		
		int[] job = new int[3];
		job[0] = 0;
		job[1] = toSmelt;
		job[2] = smeltResult;
		tile.smeltingJobs.add(job);
	}
	
	private static boolean hasSmeltingJob(TileEntityRFC tile, int toSmeltSlot) {
		
		for (int[] job : tile.smeltingJobs) {
			if (job[1] == toSmeltSlot)
				return true;
		}
		return false;
	}
	
	private static void completeSmeltingJob(TileEntityRFC tile, int[] job, int jobIndex) {
		
		if (tile.getInventory().getStackInSlot(job[1]).isEmpty()) {
			tile.smeltingJobs.remove(jobIndex);
			return;
		}
		ItemFolder.remove(tile.getInventory().getTrueStackInSlot(job[1]), 1);
		ItemFolder.add(tile.getInventory().getTrueStackInSlot(job[2]), 1);
		if (tile.getInventory().getStackInSlot(job[1]).isEmpty()) {
			tile.smeltingJobs.remove(jobIndex);
			return;
		}
		job[0] = 0;
	}
	
	public static void incrementSmeltTime(TileEntityRFC tile) {
		
		if (tile.fuelTime > 0) tile.fuelTime--;
		else return;
		
		if (tile.getWorld().isRemote) return;
		if (!tile.smeltingJobs.isEmpty()) {
			for (int i = 0; i < tile.smeltingJobs.size(); i++) {
				int[] job = tile.smeltingJobs.get(i);
				job[0]++;
				if (job[0] >= SMELT_TIME)
					completeSmeltingJob(tile, job, i);
			}
		}
	}
	
	public static String getSmeltingPercentage(TileEntityRFC tile, int slot) {
		
		if (UpgradeHelper.getUpgrade(tile, StringLibs.TAG_SMELT) == null || tile.smeltingJobs.isEmpty()) return "";
		for (int[] job : tile.smeltingJobs) {
			if (job[1] == slot) {
				NumberFormat percentFormatter = NumberFormat.getInstance();
				double calc = 0.0;
				if (job[0] > 0)
					calc = (double)job[0] / (double)SMELT_TIME;
				return " [" + percentFormatter.format(calc *= 100) + "%]";
			}
		}
		return "";
	}
	
	public static boolean isSmelting(TileEntityRFC tile) {
		
		return tile.fuelTime > 0;
	}
}

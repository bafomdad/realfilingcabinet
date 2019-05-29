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

import com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;

public class SmeltingUtils {
	
	public static final int FUEL_TIME = 1600;
	public static final int SMELT_TIME = 100;
	
	public static boolean canSmelt(TileFilingCabinet tile) {
		
		return !UpgradeHelper.getUpgrade(tile, StringLibs.TAG_SMELT).isEmpty();
	}

	public static void createSmeltingJob(TileFilingCabinet tile) {
		
		int toSmelt = -1;
		int smeltResult = -1;
		for (int i = 0; i < tile.getInventory().getSlots(); i++) {
			if (tile.getInventory().getStackInSlot(i).isEmpty()) continue;
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
					FolderUtils.get(tile.getInventory().getFolder(k)).remove(1);
					tile.fuelTime = FUEL_TIME;
					break;
				}
			}
		}
	}
	
	public static void writeSmeltNBT(TileFilingCabinet tile, NBTTagCompound tag) {

		if (!canSmelt(tile)) {
			if (!tile.smeltingJobs.isEmpty())
				tile.smeltingJobs.clear();
			tile.fuelTime = 0;
			return;
		}
		tag.setInteger(StringLibs.TAG_FUELTIME, tile.fuelTime);
		NBTTagList tagList = new NBTTagList();
		for (int[] job : tile.smeltingJobs) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setIntArray(StringLibs.TAG_SMELTJOB, job);
			tagList.appendTag(nbt);
		}
		tag.setTag(StringLibs.TAG_SMELTLIST, tagList);
	}
	
	public static void readSmeltNBT(TileFilingCabinet tile, NBTTagCompound tag) {
	
		if (tag.hasKey(StringLibs.TAG_SMELTLIST)) {

			NBTTagList tagList = tag.getTagList(StringLibs.TAG_SMELTLIST, 11);
			for (int i = 0; i < tagList.tagCount(); i++) {
				int[] job = tagList.getIntArrayAt(i);
				tile.smeltingJobs.add(job);
			}
		}
		tile.fuelTime = tag.getInteger(StringLibs.TAG_FUELTIME);
	}
	
	private static void addSmeltingJob(TileFilingCabinet tile, int toSmelt, int smeltResult) {
		
		if (tile.getWorld().isRemote) return;
		
		int[] job = new int[3];
		job[0] = 0;
		job[1] = toSmelt;
		job[2] = smeltResult;
		tile.smeltingJobs.add(job);
	}
	
	private static boolean hasSmeltingJob(TileFilingCabinet tile, int toSmeltSlot) {
		
		for (int[] job : tile.smeltingJobs) {
			if (job[1] == toSmeltSlot)
				return true;
		}
		return false;
	}
	
	private static void completeSmeltingJob(TileFilingCabinet tile, int[] job, int jobIndex) {
		
		if (tile.getInventory().getStackInSlot(job[1]).isEmpty()) {
			tile.smeltingJobs.remove(jobIndex);
			return;
		}
		FolderUtils.get(tile.getInventory().getFolder(job[1])).remove(1);
		FolderUtils.get(tile.getInventory().getFolder(job[2])).add(1);
		
		if (tile.getInventory().getStackInSlot(job[1]).isEmpty()) {
			tile.smeltingJobs.remove(jobIndex);
			return;
		}
		job[0] = 0;
	}
	
	public static void incrementSmeltTime(TileFilingCabinet tile) {
		
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
	
	public static String getSmeltingPercentage(TileFilingCabinet tile, int slot) {
		
		if (tile.smeltingJobs.isEmpty()) return "";
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
	
	public static boolean isSmelting(TileFilingCabinet tile) {
		
		return tile.fuelTime > 0;
	}
}

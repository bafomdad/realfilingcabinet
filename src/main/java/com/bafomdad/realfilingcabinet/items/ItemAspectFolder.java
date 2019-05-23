package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import thaumcraft.api.aspects.Aspect;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;

public class ItemAspectFolder extends Item implements IFolder {
	
	private static final int MAX_COUNT = 10000;
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag whatisthis) {
		
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(StringLibs.TAG_ASPECT)) {
			Aspect asp = getAspectFromFolder(stack);
			int count = getAspectCount(stack);
			list.add(asp.getName() + " x" + count);
		}
		else list.add("<Empty>");
	}
	
	public static boolean isAspectFolderEmpty(ItemStack stack) {
		
		return (!stack.hasTagCompound()) || (stack.hasTagCompound() && !stack.getTagCompound().hasKey(StringLibs.TAG_ASPECT));
	}
	
	public static Aspect getAspectFromFolder(ItemStack stack) {
		
		return Aspect.getAspect(NBTUtils.getString(stack, StringLibs.TAG_ASPECT, ""));
	}
	
	public static int getAspectCount(ItemStack stack) {
		
		return NBTUtils.getInt(stack, StringLibs.TAG_ASPECT_COUNT, 0);
	}
	
	public static void setAspect(ItemStack stack, Aspect asp) {
		
		NBTUtils.setString(stack, StringLibs.TAG_ASPECT, asp.getTag());
	}
	
	public static void setAspectCount(ItemStack stack, int amount) {
		
		NBTUtils.setInt(stack, StringLibs.TAG_ASPECT_COUNT, amount);
	}
	
	public static void incrementAspect(ItemStack stack, int amount) {
		
		int current = getAspectCount(stack);
		setAspectCount(stack, current + amount);
	}
	
	public static void decrementAspect(ItemStack stack, int amount) {
		
		int current = getAspectCount(stack);
		setAspectCount(stack, Math.max(current - amount, 0));
	}
	
	public static int getMaxAmount() {
		
		return MAX_COUNT;
	}

	@Override
	public ItemStack getEmptyFolder(ItemStack stack) {

		return new ItemStack(RFCItems.FOLDER_ASPECT);
	}

	@Override
	public Object insertIntoFolder(ItemStack folder, Object toInsert, boolean simulate) {
		// NO-OP
		return null;
	}

	@Override
	public Object extractFromFolder(ItemStack folder, long amount, boolean simulate) {
		// NO-OP
		return null;
	}
}

package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import thaumcraft.api.aspects.Aspect;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.Optional;

public class ItemAspectFolder extends Item implements IFolder {
	
	private static final String TAG_ASPECT = "RFC_aspectType";
	private static final String TAG_ASPECT_COUNT = "RFC_aspectCount";
	private static final int MAX_COUNT = 10000;
	
	public ItemAspectFolder() {
		
		setRegistryName("folder_aspect");
		setTranslationKey(RealFilingCabinet.MOD_ID + ".aspectfolder");
		setMaxStackSize(1);
		setCreativeTab(TabRFC.instance);
	}
	
	@Override
	public void addInformation(ItemStack stack, World player, List list, ITooltipFlag whatisthis) {
		
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_ASPECT)) {
			Aspect asp = getAspectFromFolder(stack);
			int count = getAspectCount(stack);
			list.add(asp.getName() + " x" + count);
		}
		else
			list.add("<Empty>");
	}
	
	@Optional.Method(modid = "thaumcraft")
	public static boolean isAspectFolderEmpty(ItemStack stack) {
		
		return (!stack.hasTagCompound()) || (stack.hasTagCompound() && !stack.getTagCompound().hasKey(TAG_ASPECT));
	}
	
	@Optional.Method(modid = "thaumcraft")
	public static Aspect getAspectFromFolder(ItemStack stack) {
		
		return Aspect.getAspect(NBTUtils.getString(stack, TAG_ASPECT, ""));
	}
	
	@Optional.Method(modid = "thaumcraft")
	public static int getAspectCount(ItemStack stack) {
		
		return NBTUtils.getInt(stack, TAG_ASPECT_COUNT, 0);
	}
	
	@Optional.Method(modid = "thaumcraft")
	public static void setAspect(ItemStack stack, Aspect asp) {
		
		NBTUtils.setString(stack, TAG_ASPECT, asp.getTag());
	}
	
	@Optional.Method(modid = "thaumcraft")
	public static void setAspectCount(ItemStack stack, int amount) {
		
		NBTUtils.setInt(stack, TAG_ASPECT_COUNT, amount);
	}
	
	@Optional.Method(modid = "thaumcraft")
	public static void addAspect(ItemStack stack, int amount) {
		
		int current = getAspectCount(stack);
		setAspectCount(stack, current + amount);
	}
	
	@Optional.Method(modid = "thaumcraft")
	public static void removeAspect(ItemStack stack, int amount) {
		
		int current = getAspectCount(stack);
		setAspectCount(stack, Math.max(current - amount, 0));
	}
	
	public static int getMaxAmount() {
		
		return MAX_COUNT;
	}

	@Override
	public ItemStack isFolderEmpty(ItemStack stack) {

		if (getAspectCount(stack) == 0)
			return stack;
		
		return ItemStack.EMPTY;
	}
}

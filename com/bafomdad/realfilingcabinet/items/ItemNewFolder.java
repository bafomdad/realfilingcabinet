package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemNewFolder extends Item implements IFolder {
	
	private static final String TAG_FILE_NAME = "fileName";
	private static final String TAG_FILE_META = "fileMeta";
	private static final String TAG_FILE_SIZE = "fileSize";
	private static final String TAG_REM_SIZE = "leftoverSize";
	
	private static final ItemStack ITEM_STORED = ItemStack.EMPTY;
	
	public enum FolderType { 
		
		NORMAL,
		ENDER,
		DURA,
		MOB,
		FLUID,
		NBT;
	}
	
	public ItemNewFolder() {
		
		setRegistryName("folder");
		setTranslationKey(RealFilingCabinet.MOD_ID + ".folder");
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(1);
	}
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		
		return getTranslationKey() + "_" + FolderType.values()[stack.getItemDamage()].toString().toLowerCase();
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		
		ItemStack copy = stack.copy();
		return copy;
	}
	
	@Override
	public boolean hasContainerItem(ItemStack stack) {
		
		return !getContainerItem(stack).isEmpty();
	}
	
	@Override
	public void addInformation(ItemStack stack, World player, List list, ITooltipFlag whatisthis) {
		
	}

	@Override
	public ItemStack isFolderEmpty(ItemStack stack) {
		// TODO Auto-generated method stub
		return ItemStack.EMPTY;
	}
}

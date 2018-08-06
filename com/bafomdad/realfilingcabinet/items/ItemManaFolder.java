package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.integration.BotaniaRFC;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;

public class ItemManaFolder extends Item implements IFolder, IManaItem, IManaTooltipDisplay {
	
	private static final String TAG_MANA_COUNT = "manaCount";
	private static final String TAG_MAX_MANA_COUNT = "maxManaCount";
	private static final int maxCount = 1000000000;

	public ItemManaFolder() {
		
		setRegistryName("folder_mana");
		setTranslationKey(RealFilingCabinet.MOD_ID + ".manafolder");
		setMaxStackSize(1);
		setCreativeTab(TabRFC.instance);
	}
	
	@Override
	public void addInformation(ItemStack stack, World player, List list, ITooltipFlag whatisthis) {
		
		int count = getManaSize(stack);
		list.add(BotaniaRFC.formatMana(count));
	}
	
	public static void setManaSize(ItemStack stack, int count) {
		
		NBTUtils.setInt(stack, TAG_MANA_COUNT, Math.max(0, count));
	}
	
	public static int getManaSize(ItemStack stack) {
		
		return NBTUtils.getInt(stack, TAG_MANA_COUNT, 0);
	}
	
	public static void addManaToFolder(ItemStack stack, int count) {
		
		int current = getManaSize(stack);
		setManaSize(stack, current + count);
	}
	
	public static boolean isManaFolderFull(ItemStack stack) {
		
		return getManaSize(stack) >= maxCount;
	}
	
	public static int getMaxManaFolder() {
		
		return maxCount;
	}

	// BOTANIA IMPLEMENTATION
	@Override
	public void addMana(ItemStack stack, int count) {

		this.addManaToFolder(stack, count);
	}

	@Override
	public boolean canExportManaToItem(ItemStack stack, ItemStack otherstack) {

		return true;
	}

	@Override
	public boolean canExportManaToPool(ItemStack stack, TileEntity pool) {

		return true;
	}

	@Override
	public boolean canReceiveManaFromItem(ItemStack stack, ItemStack otherstack) {

		return true;
	}

	@Override
	public boolean canReceiveManaFromPool(ItemStack stack, TileEntity pool) {

		return true;
	}

	@Override
	public int getMana(ItemStack stack) {

		return this.getManaSize(stack);
	}

	@Override
	public int getMaxMana(ItemStack stack) {

		return getMaxManaFolder();
	}

	@Override
	public boolean isNoExport(ItemStack stack) {

		return false;
	}

	@Override
	public float getManaFractionForDisplay(ItemStack stack) {

		return (float)getManaSize(stack) / (float) getMaxMana(stack);
	}
	
	@Override
	public ItemStack isFolderEmpty(ItemStack stack) {
		
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldstack, ItemStack newstack, boolean slotchanged) {
		
		return oldstack.getItem() != newstack.getItem();
	}
}

package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;

import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.utils.ManaStorageUtils;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;

public class ItemManaFolder extends Item implements IFolder, IManaItem, IManaTooltipDisplay {
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag whatisthis) {
		
		int count = ManaStorageUtils.getManaSize(stack);
		list.add(new TextComponentTranslation(TextHelper.formatMana(count)).getFormattedText());
	}
	
	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		
		return true;
	}
	
    @Nullable
    public Entity createEntity(World world, Entity location, ItemStack stack) {
        
    	((EntityItem)location).setNoDespawn();

    	return null;
    }
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldstack, ItemStack newstack, boolean slotchanged) {
		
		return oldstack.getItem() != newstack.getItem();
	}
	
	@Override
	public ItemStack getEmptyFolder(ItemStack stack) {

		return new ItemStack(RFCItems.FOLDER_MANA);
	}

	@Override
	public Object insertIntoFolder(ItemStack folder, Object toInsert, boolean simulate) {

		return null;
	}

	@Override
	public Object extractFromFolder(ItemStack folder, long amount, boolean simulate) {

		return null;
	}
	
	// Botania stuff start
	@Override
	public void addMana(ItemStack stack, int count) {

		ManaStorageUtils.addManaToFolder(stack, count);
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

		return ManaStorageUtils.getManaSize(stack);
	}

	@Override
	public int getMaxMana(ItemStack stack) {

		return ManaStorageUtils.getMaxManaFolder();
	}

	@Override
	public boolean isNoExport(ItemStack stack) {

		return false;
	}

	@Override
	public float getManaFractionForDisplay(ItemStack stack) {

		return (float)ManaStorageUtils.getManaSize(stack) / (float) getMaxMana(stack);
	}
}

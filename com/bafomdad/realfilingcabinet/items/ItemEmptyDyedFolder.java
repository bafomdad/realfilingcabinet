package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.api.IEmptyFolder;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;

public class ItemEmptyDyedFolder extends Item implements IEmptyFolder {

	public ItemEmptyDyedFolder() {
		
		setRegistryName("emptydyedfolder");
		setTranslationKey(RealFilingCabinet.MOD_ID + ".dyedfolder");
		setMaxStackSize(8);
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(TabRFC.instance);
	}
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		
		return getTranslationKey() + "." + EnumDyeColor.values()[stack.getItemDamage()].getName().toLowerCase();
	}
	
	@Override
	public void addInformation(ItemStack stack, World player, List<String> list, ITooltipFlag whatisthis) {
		
		list.add(TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".emptyfolder0"));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		
		if (isInCreativeTab(tab)) {
			for (int i = 0; i < EnumDyeColor.values().length; ++i)
				list.add(new ItemStack(this, 1, i));
		}
	}
}

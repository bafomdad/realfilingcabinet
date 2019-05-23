package com.bafomdad.realfilingcabinet.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.realfilingcabinet.ConfigRFC.RecipeConfig;
import com.bafomdad.realfilingcabinet.api.ISubModel;
import com.bafomdad.realfilingcabinet.api.IUpgrade;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.enums.UpgradeType;

public class ItemUpgrades extends Item implements ISubModel, IUpgrade {
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		
		return getTranslationKey() + "_" + UpgradeType.values()[stack.getItemDamage()].toString().toLowerCase();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		
		if (isInCreativeTab(tab)) {
			for (int i = 0; i < UpgradeType.values().length; ++i)
				list.add(new ItemStack(this, 1, i));
		}
	}
	
	@SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
    	
		switch (stack.getItemDamage())
		{
			case 0: return EnumRarity.EPIC;
			case 2: return EnumRarity.RARE;
			default: return EnumRarity.COMMON;
		}
    }
	
	@Override
	public boolean canApply(ItemStack upgrade, EntityPlayer player) {

		boolean flag;
		switch (upgrade.getItemDamage()) {
			case 1: flag = RecipeConfig.craftingUpgrade; break;
			case 2: flag = RecipeConfig.enderUpgrade; break;
			case 3: flag = RecipeConfig.oreDictUpgrade; break;
			case 4: flag = RecipeConfig.mobUpgrade; break;
			case 5: flag = RecipeConfig.fluidUpgrade; break;
			case 6: flag = RecipeConfig.lifeUpgrade; break;
			case 7: flag = RecipeConfig.smeltingUpgrade; break;
			default: flag = true;
		}
		if (!flag)
			player.sendStatusMessage(new TextComponentTranslation(StringLibs.MESSAGE + ".disabled"), true);
		return flag;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerSubModels(Item item) {

		for (int i = 0; i < UpgradeType.values().length; ++i)
			ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(item.getRegistryName() + "_" + UpgradeType.values()[i].toString().toLowerCase(), "inventory"));
	}
}

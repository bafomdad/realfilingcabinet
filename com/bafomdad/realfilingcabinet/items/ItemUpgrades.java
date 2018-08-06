package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import com.bafomdad.realfilingcabinet.NewConfigRFC.ConfigRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.api.IUpgrades;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemUpgrades extends Item implements IUpgrades {
	
	public enum UpgradeType {
		CREATIVE,
		CRAFTING,
		ENDER,
		OREDICT,
		MOB,
		FLUID,
		LIFE,
		SMELTING;
	}
	
	public ItemUpgrades() {
		
		setRegistryName("upgrade");
		setTranslationKey(RealFilingCabinet.MOD_ID + ".upgrade");
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(16);
		setCreativeTab(TabRFC.instance);
	}
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		
		return getTranslationKey() + "_" + UpgradeType.values()[stack.getItemDamage()].toString().toLowerCase();
	}
	
	@Override
	public void addInformation(ItemStack stack, World player, List list, ITooltipFlag whatisthis) {
		
		if (stack.getItemDamage() == UpgradeType.CRAFTING.ordinal() && !ConfigRFC.craftingUpgrade)
			list.add(TextHelper.localize("message." + RealFilingCabinet.MOD_ID + ".disabled"));
		if (stack.getItemDamage() == UpgradeType.ENDER.ordinal() && !ConfigRFC.enderUpgrade)
			list.add(TextHelper.localize("message." + RealFilingCabinet.MOD_ID + ".disabled"));
		if (stack.getItemDamage() == UpgradeType.OREDICT.ordinal() && !ConfigRFC.oreDictUpgrade)
			list.add(TextHelper.localize("message." + RealFilingCabinet.MOD_ID + ".disabled"));
		if (stack.getItemDamage() == UpgradeType.MOB.ordinal() && !ConfigRFC.mobUpgrade)
			list.add(TextHelper.localize("message." + RealFilingCabinet.MOD_ID + ".disabled"));
		if (stack.getItemDamage() == UpgradeType.FLUID.ordinal() && !ConfigRFC.fluidUpgrade)
			list.add(TextHelper.localize("message." + RealFilingCabinet.MOD_ID + ".disabled"));
		if (stack.getItemDamage() == UpgradeType.LIFE.ordinal() && !ConfigRFC.lifeUpgrade)
			list.add(TextHelper.localize("message." + RealFilingCabinet.MOD_ID + ".disabled"));
		if (stack.getItemDamage() == UpgradeType.SMELTING.ordinal() && !ConfigRFC.smeltingUpgrade)
			list.add(TextHelper.localize("message." + RealFilingCabinet.MOD_ID + ".disabled"));
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
	public boolean canApply(TileEntityRFC tile, ItemStack upgrade, EntityPlayer player) {

		switch (upgrade.getItemDamage()) {
			case 1: if (!ConfigRFC.craftingUpgrade) return false;
			case 2: if (!ConfigRFC.enderUpgrade) return false;
			case 3: if (!ConfigRFC.oreDictUpgrade) return false;
			case 4: if (!ConfigRFC.mobUpgrade) return false;
			case 5: if (!ConfigRFC.fluidUpgrade) return false;
			case 6: if (!ConfigRFC.lifeUpgrade) return false;
			case 7: if (!ConfigRFC.smeltingUpgrade) return false; 
		}
		
		if (upgrade.getItemDamage() == 0) {
			return !UpgradeHelper.isCreative(tile);
		}
		if (upgrade.getItemDamage() == 2) {
			for (ItemStack stack : tile.getInventory().getStacks()) {
				if (!stack.isEmpty() && /*stack.getItem() instanceof ItemManaFolder ||*/ (stack.getItem() instanceof ItemFolder && stack.getItemDamage() > 0)) {
					player.sendStatusMessage(new TextComponentString(TextHelper.localize("message." + RealFilingCabinet.MOD_ID + ".errorEnder")), true);
					return false;
				}
			}
		}
		return true;
	}
}

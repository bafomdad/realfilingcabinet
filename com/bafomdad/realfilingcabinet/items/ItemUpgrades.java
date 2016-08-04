package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemUpgrades extends Item {

	public String[] upgradeTypes = new String[] { "upgrade_creative", "upgrade_crafting", "upgrade_ender" };
	public IIcon[] iconArray;
	
	public ItemUpgrades() {
		
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(16);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		
		iconArray = new IIcon[upgradeTypes.length];
		for (int i = 0; i < iconArray.length; i++) {
			iconArray[i] = register.registerIcon(RealFilingCabinet.MOD_ID + ":" + upgradeTypes[i]);
		}
	}
	
	public IIcon getIconFromDamage(int meta) {
		
		return this.iconArray[meta];
	}
	
	public String getUnlocalizedName(ItemStack stack) {
		
		return "item." + RealFilingCabinet.MOD_ID + "." + upgradeTypes[stack.getItemDamage()];
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		
		for (int i = 0; i < upgradeTypes.length; ++i)
			list.add(new ItemStack(item, 1, i));
	}
}

package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.api.IUpgrades;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemUpgrades extends Item implements IUpgrades {
	
	public String[] upgradeTypes = new String[] { "creative", "crafting", "ender", "oredict", "mob", "fluid", "life" };

	public ItemUpgrades() {
		
		setRegistryName("upgrade");
		setUnlocalizedName(RealFilingCabinet.MOD_ID + ".upgrade");
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(16);
		setCreativeTab(TabRFC.instance);
		GameRegistry.register(this);
	}
	
	public String getUnlocalizedName(ItemStack stack) {
		
		return getUnlocalizedName() + "_" + upgradeTypes[stack.getItemDamage()];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
		
		for (int i = 0; i < upgradeTypes.length; ++i)
			list.add(new ItemStack(item, 1, i));
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

		if (upgrade.getItemDamage() == 2) {
			for (ItemStack stack : tile.getInventory().getStacks()) {
				if (stack != ItemStack.EMPTY)
					break;
				else {
					player.sendMessage(new TextComponentString(TextHelper.localize("message." + RealFilingCabinet.MOD_ID + ".errorEnder")));
					return false;
				}
			}
		}
		if (upgrade.getItemDamage() == 5) {
			for (ItemStack stack : tile.getInventory().getStacks()) {
				if (stack != ItemStack.EMPTY) {
					player.sendMessage(new TextComponentString(TextHelper.localize("message." + RealFilingCabinet.MOD_ID + ".errorFluid")));
					return false;
				}
			}
		}
		return true;
	}
}

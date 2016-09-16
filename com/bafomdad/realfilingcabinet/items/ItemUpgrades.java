package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.TabRFC;
import com.bafomdad.realfilingcabinet.api.IUpgrades;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemUpgrades extends Item implements IUpgrades {
	
	public String[] upgradeTypes = new String[] { "creative", "crafting", "ender", "oredict" };

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
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		
		for (int i = 0; i < upgradeTypes.length; ++i)
			list.add(new ItemStack(item, 1, i));
	}
}

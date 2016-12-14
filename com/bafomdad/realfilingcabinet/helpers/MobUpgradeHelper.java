package com.bafomdad.realfilingcabinet.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bafomdad.realfilingcabinet.api.IUpgrades;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.entity.EntityCabinet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class MobUpgradeHelper {

	private static Map<ItemStack, String> mobUpgrades = new HashMap<ItemStack, String>();
	
	/**
	 * Entity cabinet version of upgrades
	 * @param stack
	 * @param tag
	 */
	public static void registerMobUpgrade(ItemStack stack, String tag) {
		
		if (stack.getItem() instanceof IUpgrades && !tag.isEmpty())
			mobUpgrades.put(stack, tag);
		else throw new IllegalArgumentException("[RealFilingCabinet]: Register mob upgrades: ItemStack is not an instance of IUpgrades, or the string tag is empty");
	}
	
	public static boolean hasMobUpgrade(EntityCabinet entity) {
		
		return !entity.upgrades.isEmpty();
	}
	
	public static String getMobUpgrade(EntityCabinet entity, String str) {
		
		if (entity.upgrades.isEmpty())
			return null;
		
		String tag = entity.upgrades;
		if (tag.equals(str))
			return tag;
		
		return null;
	}
	
	public static void setUpgrade(EntityPlayer player, EntityCabinet cabinet, ItemStack upgrade) {
		
		if (cabinet.world.isRemote || !(upgrade.getItem() instanceof IUpgrades))
			return;
		
		if (hasMobUpgrade(cabinet))
			return;
		
		String key = stringTest(upgrade);
		if (key != null)
		{
			cabinet.upgrades = key;
			if (!player.capabilities.isCreativeMode)
				upgrade.shrink(1);
			if (key.equals(StringLibs.TAG_MOB))
				cabinet.setTextureState(1);
			else if (key.equals(StringLibs.TAG_FLUID))
				cabinet.setTextureState(2);
		}
	}
	
	public static void removeUpgrade(EntityPlayer player, EntityCabinet cabinet) {
		
		if (!hasMobUpgrade(cabinet))
			return;
		
		ItemStack upgrade = stackTest(cabinet);
		if (upgrade != null)
		{
			ItemStack newStack = new ItemStack(upgrade.getItem(), 1, upgrade.getItemDamage());
			if (!player.inventory.addItemStackToInventory(newStack))
				player.dropItem(newStack.getItem(), 1);
		}
		cabinet.setTextureState(0);
		cabinet.upgrades = "";
	}
	
	private static String stringTest(ItemStack upgrade) {
		
		List<ItemStack> keys = new ArrayList(mobUpgrades.keySet());
		for (ItemStack is : keys) {
			if (ItemStack.areItemsEqual(upgrade, is)) {
				String str = mobUpgrades.get(is);
				return str;
			}
		}
		return null;
	}
	
	public static ItemStack stackTest(EntityCabinet cabinet) {
		
		String str = cabinet.upgrades;
		if (str.isEmpty())
			return null;
		
		for (Map.Entry<ItemStack, String> entry : mobUpgrades.entrySet())
		{
			String value = entry.getValue();
			if (value.equals(str))
				return entry.getKey();
		}
		return null;
	}
}

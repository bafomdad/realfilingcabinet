package com.bafomdad.realfilingcabinet.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import com.bafomdad.realfilingcabinet.api.IFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IUpgrades;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.init.RFCItems;

public class UpgradeHelper {
	
	private static Map<ItemStack, String> upgrades = new HashMap<ItemStack, String>();

	/**
	 * Put this in init phase or sometime after you've registered all your items. Stack must implement IUpgrades, and tag must not be empty.
	 * @param stack
	 * @param tag
	 */
	public static void registerUpgrade(ItemStack stack, String tag) {
		
		if (stack.getItem() instanceof IUpgrades && !tag.isEmpty())
			upgrades.put(stack, tag);
		else throw new IllegalArgumentException("[RealFilingCabinet]: Register upgrades: ItemStack is not an instance of IUpgrades, or the string tag is empty");
	}
	
	/**
	 * Conditional checking for whether the tile already has an upgrade installed, excluding creative upgrade
	 * @param tile
	 * @return
	 */
	public static boolean hasUpgrade(TileEntity tile) {
		
		if (!(tile.getBlockType() instanceof IFilingCabinet))
			return false;
		
		return tile.getTileData().hasKey(StringLibs.RFC_UPGRADE);
	}
	
	/**
	 * Conditional checking for whether the tile has an creative upgrade installed
	 * @param tile
	 * @return
	 */
	public static boolean isCreative(TileEntity tile) {
		
		return tile.getTileData().getBoolean(StringLibs.TAG_CREATIVE);
	}
	
	/**
	 * Check if the tile has an upgrade installed, using a nbt tag string
	 * @param tile
	 * @param tag
	 * @return String
	 */
	public static String getUpgrade(TileEntity tile, String tag) {
		
		if (!hasUpgrade(tile))
			return null;
		
		String str = tile.getTileData().getString(StringLibs.RFC_UPGRADE);
		if (str.equals(tag))
			return str;
		
		return null;
	}
	
	/**
	 * Sets the upgrade into the tile, if it allows for it, including creative upgrade
	 * @param player
	 * @param tile
	 * @param upgrade
	 */
	public static void setUpgrade(EntityPlayer player, TileEntity tile, ItemStack upgrade) {
		
		if (tile.getWorld().isRemote || !(upgrade.getItem() instanceof IUpgrades))
			return;
		
		if (!((IUpgrades)upgrade.getItem()).canApply(tile, upgrade))
			return;

		NBTTagCompound tileTag = tile.getTileData();
		String key = stringTest(upgrade);
		
		if (key != null && key.equals(StringLibs.TAG_CREATIVE))
		{
			if (tileTag.getBoolean(StringLibs.TAG_CREATIVE))
				return;
			
			tileTag.setBoolean(StringLibs.TAG_CREATIVE, true);
			if (!player.capabilities.isCreativeMode)
				upgrade.stackSize--;
			tile.markDirty();
			return;
		}
		
		if (hasUpgrade(tile))
			return;
			
		if (key != null)
		{
			tileTag.setString(StringLibs.RFC_UPGRADE, key);
			if (!player.capabilities.isCreativeMode)
				upgrade.stackSize--;
			if (upgrade.getItem() == RFCItems.upgrades && upgrade.getItemDamage() == 2 && tile instanceof TileEntityRFC)
				((TileEntityRFC)tile).setHash(tile);
			tile.markDirty();
		}
	}
	
	/**
	 * Removes an upgrade from the tile, including creative upgrade
	 * @param player
	 * @param tile
	 */
	public static void removeUpgrade(EntityPlayer player, TileEntity tile) {
		
		if ((!hasUpgrade(tile) && !isCreative(tile)))
			return;

		ItemStack creative = creativeTest(tile);
		if (creative != null)
		{
			tile.getTileData().setBoolean(StringLibs.TAG_CREATIVE, false);
			if (!player.inventory.addItemStackToInventory(creative))
				player.dropItem(creative.getItem(), 1);
			tile.markDirty();
			return;
		}
		ItemStack upgrade = stackTest(tile);
		if (upgrade != null)
		{
			tile.getTileData().removeTag(StringLibs.RFC_UPGRADE);
			ItemStack newStack = new ItemStack(upgrade.getItem(), 1, upgrade.getItemDamage());
			if (!player.inventory.addItemStackToInventory(newStack))
				player.dropItem(newStack.getItem(), 1);
			tile.markDirty();
		}
		else
			tile.getTileData().removeTag(StringLibs.RFC_UPGRADE);
	}
	
	private static String stringTest(ItemStack upgrade) {
		
		List<ItemStack> keys = new ArrayList(upgrades.keySet());
		for (ItemStack is : keys) {
			if (ItemStack.areItemsEqual(upgrade, is)) {
				String str = upgrades.get(is);
				return str;
			}
		}
		return null;
	}
	
	public static ItemStack stackTest(TileEntity tile) {
		
		String str = tile.getTileData().getString(StringLibs.RFC_UPGRADE);
		
		for (Map.Entry<ItemStack, String> entry : upgrades.entrySet())
		{
			String value = entry.getValue();
			if (value.equals(str)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	private static ItemStack creativeTest(TileEntity tile) {
		
		boolean bool = tile.getTileData().getBoolean(StringLibs.TAG_CREATIVE);
		if (bool)
		{
			return new ItemStack(RFCItems.upgrades, 1, 0);
		}
		return null;
	}
}

package com.bafomdad.realfilingcabinet.core;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.blocks.TileEntityRFC;

public class UpgradeHandler {

	public static void handleUpgrade(TileEntityRFC tile, ItemStack stack, EntityPlayer player) {
		
		if (stack.getItemDamage() == 0)
		{
			if (tile.isCreative)
				return;
			tile.isCreative = true;
			tile.getWorldObj().markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
			if (!player.capabilities.isCreativeMode)
				stack.stackSize--;
		}
		if (stack.getItemDamage() == 1)
		{
			if (tile.isAutoCraft)
				return;
			tile.isAutoCraft = true;
			tile.getWorldObj().markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
			if (!player.capabilities.isCreativeMode)
				stack.stackSize--;
		}
		if (stack.getItemDamage() == 2)
		{
			if (tile.isAutoCraft || tile.isEnder || tile.getStackInSlot(0) == null)
				return;
			
			tile.isEnder = true;
			tile.getWorldObj().markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
			if (!player.capabilities.isCreativeMode)
				stack.stackSize--;
		}
	}
	
	public static void dropUpgrade(TileEntityRFC tile) {
		
		if (tile.isAutoCraft)
		{
			ItemStack stack = new ItemStack(RealFilingCabinet.itemUpgrades, 1, 1);
			tile.getWorldObj().spawnEntityInWorld(new EntityItem(tile.getWorldObj(), tile.xCoord + 0.5, tile.yCoord + 0.5, tile.zCoord + 0.5, stack));
		}
		if (tile.isEnder)
		{
			ItemStack stack = new ItemStack(RealFilingCabinet.itemUpgrades, 1, 2);
			tile.getWorldObj().spawnEntityInWorld(new EntityItem(tile.getWorldObj(), tile.xCoord + 0.5, tile.yCoord + 0.5, tile.zCoord + 0.5, stack));
		}
	}
}

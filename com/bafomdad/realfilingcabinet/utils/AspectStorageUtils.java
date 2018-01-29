package com.bafomdad.realfilingcabinet.utils;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.items.ItemHandlerHelper;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityAC;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemAspectFolder;

public class AspectStorageUtils {
	
	public static void addAspect(TileEntityAC tile, EntityPlayer player, ItemStack stack) {
		
		if (tile.getWorld().isRemote) return;
		
		AspectList al = ((IEssentiaContainerItem)stack.getItem()).getAspects(stack);
		if (al != null) {
			Aspect asp = al.getAspects()[0];
			ItemStack folder = tile.getAspectFolder(asp);
			if (folder != null) {
				int aspectAmount = al.getAmount(asp);
				if (!player.capabilities.isCreativeMode) {
					int add = tile.addToContainer(asp, aspectAmount);
					if (add <= 0) {
						stack.stackSize--;
						ItemStack newPhial = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("thaumcraft", "phial")), 1, 0);
						if (stack.stackSize == 0) {
							player.setHeldItem(EnumHand.MAIN_HAND, null);
						}
						ItemHandlerHelper.giveItemToPlayer(player, newPhial);
					}
					else {
						stack.stackSize--;
						ItemStack newPhial = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("thaumcraft", "phial")), 1, 1);
						((IEssentiaContainerItem)newPhial.getItem()).setAspects(stack, new AspectList().add(asp, tile.addToContainer(asp, aspectAmount)));
						ItemHandlerHelper.giveItemToPlayer(player, newPhial);
					}
				}
				else
					tile.addToContainer(asp, aspectAmount);
				tile.markBlockForUpdate();
			}
		}
	}

	public static void extractAspect(TileEntityAC tile, EntityPlayer player, boolean crouching) {
		
		if (tile.getWorld().isRemote) return;
		
		ItemStack stack = tile.getFilter();
		int essentia = getPhial(player);
		if (stack == null || essentia == -1) return;
		
		ItemStack phial = player.inventory.mainInventory[essentia];
		if (stack.getItem() == RFCItems.aspectFolder) {
			Aspect asp = ItemAspectFolder.getAspectFromFolder(stack);
			if (ItemAspectFolder.getAspectCount(stack) >= 10) {
				ItemStack newPhial = phial.copy();
				phial.stackSize--;
				if (phial.stackSize == 0) {
					player.inventory.setInventorySlotContents(essentia, null);
					//StorageUtils.updatePlayerInventory(player);
				}
				newPhial.stackSize = 1;
				newPhial.setItemDamage(1);
				((IEssentiaContainerItem)phial.getItem()).setAspects(newPhial, new AspectList().add(asp, 10));
				ItemAspectFolder.removeAspect(stack, 10);
				ItemHandlerHelper.giveItemToPlayer(player, newPhial);
				return;
			}
		}
		else if (stack.getItem() instanceof IEssentiaContainerItem) {
			AspectList al = ((IEssentiaContainerItem)stack.getItem()).getAspects(stack);
			if (al != null) {
				Aspect asp = al.getAspects()[0];
				ItemStack aspectFolder = tile.getAspectFolder(asp);
				if (aspectFolder != null && ItemAspectFolder.getAspectCount(aspectFolder) >= 10) {
					ItemStack newPhial = phial.copy();
					phial.stackSize--;
					if (phial.stackSize == 0) {
						player.inventory.setInventorySlotContents(essentia, null);
						//StorageUtils.updatePlayerInventory(player);
					}
					newPhial.stackSize = 1;
					newPhial.setItemDamage(1);
					((IEssentiaContainerItem)phial.getItem()).setAspects(newPhial, new AspectList().add(asp, 10));
					ItemAspectFolder.removeAspect(aspectFolder, 10);
					ItemHandlerHelper.giveItemToPlayer(player, newPhial);
					return;
				}
			}
		}
	}
	
	public static AspectList getFirstAspectStored(TileEntityAC tile, ItemStack filter) {
		
		if (filter.getItem() == RFCItems.aspectFolder) {
			Aspect asp = ItemAspectFolder.getAspectFromFolder(filter);
			int count = ItemAspectFolder.getAspectCount(filter);
			return new AspectList().add(asp, count);
		}
		else if (filter.getItem() instanceof IEssentiaContainerItem) {
			AspectList al = ((IEssentiaContainerItem)filter.getItem()).getAspects(filter);
			if (al != null) {
				Aspect asp = al.getAspects()[0];
				ItemStack aspectFolder = tile.getAspectFolder(asp);
				if (aspectFolder != null) {
					int count = ItemAspectFolder.getAspectCount(aspectFolder);
					return new AspectList().add(asp, count);
				}
			}
		}
		return null;
	}
	
	private static int getPhial(EntityPlayer player) {
		
		for (int i = 0; i < player.inventory.mainInventory.length; i++) {
			ItemStack stack = player.inventory.mainInventory[i];
			if (stack != null && (stack.getItem() == ForgeRegistries.ITEMS.getValue(new ResourceLocation("thaumcraft", "phial")) && stack.getItemDamage() == 0))
				return i;
		}
		return -1;
	}
}

package com.bafomdad.realfilingcabinet.utils;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemHandlerHelper;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileAspectCabinet;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemAspectFolder;

public class AspectStorageUtils {
	
	public static void addAspect(TileAspectCabinet tile, EntityPlayer player, ItemStack stack) {
		
		if (tile.getWorld().isRemote) return;
		
		AspectList al = ((IEssentiaContainerItem)stack.getItem()).getAspects(stack);
		if (al != null) {
			Aspect asp = al.getAspects()[0];
			ItemStack folder = tile.getAspectFolder(asp);
			if (!folder.isEmpty()) {
				int aspectAmount = al.getAmount(asp);
				if (!player.capabilities.isCreativeMode) {
					int add = tile.addToContainer(asp, aspectAmount);
					if (add <= 0) {
						stack.shrink(1);
						ItemStack newPhial = new ItemStack(RFCItems.PHIAL, 1, 0);
						ItemHandlerHelper.giveItemToPlayer(player, newPhial);
					}
					else {
						stack.shrink(1);
						ItemStack newPhial = new ItemStack(RFCItems.PHIAL, 1, 1);
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

	public static void extractAspect(TileAspectCabinet tile, EntityPlayer player, boolean crouching) {
		
		if (tile.getWorld().isRemote) return;
		
		ItemStack stack = tile.getFilter();
		int essentia = getPhial(player);
		if (stack.isEmpty() || essentia == -1) return;
		
		ItemStack phial = player.inventory.mainInventory.get(essentia);
		if (stack.getItem() == RFCItems.FOLDER_ASPECT) {
			Aspect asp = ItemAspectFolder.getAspectFromFolder(stack);
			if (ItemAspectFolder.getAspectCount(stack) >= 10) {
				ItemStack newPhial = phial.copy();
				newPhial.setCount(1);
				newPhial.setItemDamage(1);
				((IEssentiaContainerItem)phial.getItem()).setAspects(newPhial, new AspectList().add(asp, 10));
				phial.shrink(1);
				ItemAspectFolder.decrementAspect(stack, 10);
				ItemHandlerHelper.giveItemToPlayer(player, newPhial);
				return;
			}
		}
		else if (stack.getItem() instanceof IEssentiaContainerItem) {
			AspectList al = ((IEssentiaContainerItem)stack.getItem()).getAspects(stack);
			if (al != null) {
				Aspect asp = al.getAspects()[0];
				ItemStack aspectFolder = tile.getAspectFolder(asp);
				if (!aspectFolder.isEmpty() && ItemAspectFolder.getAspectCount(aspectFolder) >= 10) {
					ItemStack newPhial = phial.copy();
					newPhial.setCount(1);
					newPhial.setItemDamage(1);
					((IEssentiaContainerItem)phial.getItem()).setAspects(newPhial, new AspectList().add(asp, 10));
					phial.shrink(1);
					ItemAspectFolder.decrementAspect(aspectFolder, 10);
					ItemHandlerHelper.giveItemToPlayer(player, newPhial);
					return;
				}
			}
		}
	}
	
	public static AspectList getFirstAspectStored(TileAspectCabinet tile, ItemStack filter) {
		
		if (filter.getItem() == RFCItems.FOLDER_ASPECT) {
			Aspect asp = ItemAspectFolder.getAspectFromFolder(filter);
			int count = ItemAspectFolder.getAspectCount(filter);
			return new AspectList().add(asp, count);
		}
		else if (filter.getItem() instanceof IEssentiaContainerItem) {
			AspectList al = ((IEssentiaContainerItem)filter.getItem()).getAspects(filter);
			if (al != null) {
				Aspect asp = al.getAspects()[0];
				ItemStack aspectFolder = tile.getAspectFolder(asp);
				if (!aspectFolder.isEmpty()) {
					int count = ItemAspectFolder.getAspectCount(aspectFolder);
					return new AspectList().add(asp, count);
				}
			}
		}
		return null;
	}
	
	private static int getPhial(EntityPlayer player) {
		
		for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
			ItemStack stack = player.inventory.mainInventory.get(i);
			if (!stack.isEmpty() && (stack.getItem() == RFCItems.PHIAL && stack.getItemDamage() == 0))
				return i;
		}
		return -1;
	}
}

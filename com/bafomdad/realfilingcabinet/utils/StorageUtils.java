package com.bafomdad.realfilingcabinet.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.helpers.UpgradeHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.inventory.FluidRFC;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

public class StorageUtils {
	
	public static int simpleFolderMatch(TileEntityRFC tile, ItemStack stack) {
		
		if (stack == ItemStack.EMPTY)
			return -1;
		
		for (int i = 0; i < tile.getInventory().getSlots(); i++) {
			ItemStack loopinv = tile.getInventory().getStackFromFolder(i);
			if (UpgradeHelper.getUpgrade(tile, StringLibs.TAG_OREDICT) != null)
			{
				OreDictUtils.recreateOreDictionary(stack);
				if (OreDictUtils.hasOreDict())
				{
					if (loopinv != ItemStack.EMPTY && OreDictUtils.areItemsEqual(stack, loopinv)) {
						return i;
					}
				}
			}
			if (loopinv != ItemStack.EMPTY && simpleMatch(stack, loopinv))
				return i;
		}
		return -1;
	}

	public static void addStackManually(TileEntityRFC tile, EntityPlayer player, ItemStack stack) {
		
		if (tile.getWorld().isRemote)
			return;
		
		if (UpgradeHelper.getUpgrade(tile, StringLibs.TAG_FLUID) != null) {
			FluidStack fluid = FluidUtil.getFluidContained(stack);
			if (fluid != null)
			{
				FluidActionResult far = FluidUtil.tryEmptyContainer(stack, tile.getFluidInventory(), fluid.amount, player, true);
				if (far.success && !player.capabilities.isCreativeMode)
					player.setHeldItem(EnumHand.MAIN_HAND, far.getResult());
				
				return;
			}
		}
		if (stack.hasTagCompound())
			return;
	
		for (int i = 0; i < tile.getInventory().getSlots(); i++) {
			ItemStack loopinv = tile.getInventory().getStackFromFolder(i);
			if (UpgradeHelper.getUpgrade(tile, StringLibs.TAG_OREDICT) != null)
			{
				OreDictUtils.recreateOreDictionary(stack);
				if (OreDictUtils.hasOreDict()) {
					if (loopinv != ItemStack.EMPTY && OreDictUtils.areItemsEqual(stack, loopinv))
					{
						ItemFolder.add(tile.getInventory().getTrueStackInSlot(i), stack.getCount());
						player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
						tile.markBlockForUpdate();
						break;
					}
				}
			}
			if ((tile.getInventory().getTrueStackInSlot(i) != ItemStack.EMPTY && tile.getInventory().getTrueStackInSlot(i).getItemDamage() == 2) && DurabilityUtils.matchDurability(tile, stack))
			{
				player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
				tile.markBlockForUpdate();
				break;
			}
			if (loopinv != ItemStack.EMPTY && simpleMatch(loopinv, stack))
			{
				ItemFolder.add(tile.getInventory().getTrueStackInSlot(i), stack.getCount());
				player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
				tile.markBlockForUpdate();
				break;
			}
		}
	}
	
	public static void addAllStacksManually(TileEntityRFC tile, EntityPlayer player) {
		
		if (tile.getWorld().isRemote)
			return;
		
		boolean consume = false;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack loopinv = player.inventory.getStackInSlot(i);
			if (loopinv != ItemStack.EMPTY && (loopinv.getItem() != RFCItems.emptyFolder || loopinv.getItem() != RFCItems.folder))
			{
				if (UpgradeHelper.getUpgrade(tile, StringLibs.TAG_FLUID) != null)
				{
					FluidStack fluid = FluidUtil.getFluidContained(loopinv);
					if (fluid != null)
					{
						FluidActionResult far = FluidUtil.tryEmptyContainer(loopinv, tile.getFluidInventory(), fluid.amount, player, true);
						if (far.success)
						{
							loopinv.shrink(1);
							player.inventory.addItemStackToInventory(far.getResult());
						}
						if (!consume)
							consume = true;
					}
				}
				if (loopinv.hasTagCompound())
					continue;
				
				for (int j = 0; j < tile.getInventory().getSlots(); j++) {
					ItemStack folderstack = tile.getInventory().getStackFromFolder(j);
					if (tile.getInventory().getTrueStackInSlot(j) != ItemStack.EMPTY && tile.getInventory().getTrueStackInSlot(j).getItemDamage() == 2 && DurabilityUtils.matchDurability(tile, loopinv))
					{
						player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
						consume = true;
						break;
					}
					if (ItemStack.areItemsEqual(folderstack, loopinv))
					{
						ItemFolder.add(tile.getInventory().getTrueStackInSlot(j), loopinv.getCount());
						player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
						consume = true;
						break;
					}
				}
			}
		}
		if (consume)
		{
			updatePlayerInventory(player);
			tile.markDirty();
		}
	}
	
	public static void extractStackManually(TileEntityRFC tile, EntityPlayer player, boolean crouching) {
		
		ItemStack stack = tile.getFilter();
		if (stack != ItemStack.EMPTY) {
			for (int i = 0; i < tile.getInventory().getSlots(); i++) {
				ItemStack loopinv = tile.getInventory().getStackFromFolder(i);
				if (UpgradeHelper.getUpgrade(tile, StringLibs.TAG_FLUID) != null)
				{
					ItemStack container = player.getHeldItemMainhand();
					if (container != ItemStack.EMPTY && container.getItem() == Items.BUCKET)
					{
						FluidStack fluid = FluidUtil.getFluidContained(stack);
						if (fluid != null)
						{
							FluidActionResult far = FluidUtil.tryFillContainer(container, tile.getFluidInventory(), Fluid.BUCKET_VOLUME, player, true);
							if (far.success) {
								container.shrink(1);
								if (!player.inventory.addItemStackToInventory(far.getResult()))
									player.dropItem(far.getResult(), true);
							}
							return;
						}
					}
					else return;
				}
				if (UpgradeHelper.getUpgrade(tile, StringLibs.TAG_OREDICT) != null)
				{
					OreDictUtils.recreateOreDictionary(stack);
					if (OreDictUtils.hasOreDict()) {
						if (loopinv != ItemStack.EMPTY && OreDictUtils.areItemsEqual(stack, loopinv))
						{
							ItemStack folder = tile.getInventory().getTrueStackInSlot(i);
							long count = ItemFolder.getFileSize(folder);
							if (crouching) {
								long extract = Math.min(stack.getMaxStackSize(), count);
								ItemStack stackExtract = new ItemStack(stack.getItem(), (int)extract, stack.getItemDamage());
								if (!player.inventory.addItemStackToInventory(stackExtract))
									player.dropItem(stackExtract, true);
								ItemFolder.remove(folder, extract);
								tile.markBlockForUpdate();
								break;
							}
							else
							{
								ItemStack stackExtract = new ItemStack(stack.getItem(), 1, stack.getItemDamage());
								if (!player.inventory.addItemStackToInventory(stackExtract))
									player.dropItem(stackExtract, true);
								ItemFolder.remove(folder, 1);
								tile.markBlockForUpdate();
								break;
							}
						}
					}
				}
				if (loopinv != ItemStack.EMPTY && simpleMatch(loopinv, stack))
				{
					ItemStack folder = tile.getInventory().getTrueStackInSlot(i);
					long count = ItemFolder.getFileSize(folder);
					if (crouching) {
						long extract = Math.min(stack.getMaxStackSize(), count);
						ItemStack stackExtract = new ItemStack(stack.getItem(), (int)extract, stack.getItemDamage());
						if (!player.inventory.addItemStackToInventory(stackExtract))
							player.dropItem(stackExtract, true);
						ItemFolder.remove(folder, extract);
						tile.markBlockForUpdate();
						break;
					}
					else
					{
						ItemStack stackExtract = new ItemStack(stack.getItem(), 1, stack.getItemDamage());
						if (!player.inventory.addItemStackToInventory(stackExtract))
							player.dropItem(stackExtract, true);
						ItemFolder.remove(folder, 1);
						tile.markBlockForUpdate();
						break;
					}
				}
			}
		}
	}
	
	public static boolean simpleMatch(ItemStack stack1, ItemStack stack2) {
		
		return stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage();
	}
	
	public static void updatePlayerInventory(EntityPlayer player) {
		
		if (player instanceof EntityPlayerMP)
			((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
	}
	
	public static void checkTapeNBT(ItemStack stack, boolean setTaped) {
		
		if (stack.getItemDamage() == 1)
			return;
		
		NBTUtils.setBoolean(stack, StringLibs.RFC_TAPED, setTaped);
	}
	
	public static void folderExtract(TileEntityRFC tile, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		if (side == EnumFacing.UP || side == EnumFacing.DOWN) {
			for (int i = tile.getInventory().getSlots() - 1; i >= 0; i--)
			{
				ItemStack folder = tile.getInventory().getTrueStackInSlot(i);
				if (folder != ItemStack.EMPTY)
				{
					tile.getInventory().setStackInSlot(i, ItemStack.EMPTY);
					player.setHeldItem(EnumHand.MAIN_HAND, folder);
					tile.markBlockForUpdate();
					break;
				}
			}
		}
		else {
			float l = hitY * 7;
			int slot = Math.round(l);
			
			ItemStack folder = tile.getInventory().getTrueStackInSlot(slot);
			if (folder != ItemStack.EMPTY)
			{
				tile.getInventory().setStackInSlot(slot, ItemStack.EMPTY);
				player.setHeldItem(EnumHand.MAIN_HAND, folder);
				tile.markBlockForUpdate();
			}
		}
	}
}

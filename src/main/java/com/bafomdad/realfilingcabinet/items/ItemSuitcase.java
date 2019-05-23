package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import javax.annotation.Nonnull;

import org.lwjgl.input.Keyboard;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.blocks.tiles.TileEntityRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.inventory.InventorySuitcase;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemSuitcase extends Item {

	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag whatisthis) {
	
//		list.add(TextFormatting.GOLD + "Current place index: " + TextFormatting.RESET + StorageUtils.getIndex(stack));
		if (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)) {
			IItemHandlerModifiable suitcaseInv = getSuitcaseInv(stack);
			if (suitcaseInv != null) {
				for (int i = 0; i < suitcaseInv.getSlots(); i++) {
					ItemStack folder = suitcaseInv.getStackInSlot(i);
					if (folder.isEmpty() || !(folder.getItem() instanceof IFolder))
						list.add("<Empty>");
					else
						FolderUtils.get(suitcaseInv.getStackInSlot(i)).addTooltips(list);
				}
			}
		}
		if (!list.isEmpty()) {
			for (int j = 1; j < list.size(); j++) {
				if (StorageUtils.getIndex(stack) + 1 == j)
					list.set(j, TextFormatting.GOLD + list.get(j));
			}
		}
	}
	
	@Override
	public NBTTagCompound getNBTShareTag(ItemStack stack) {
		
		if (!stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
			return super.getNBTShareTag(stack);
			
		NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound().copy() : new NBTTagCompound();
		IItemHandler inv = getSuitcaseInv(stack);
		tag.setTag(StringLibs.TAG_FOLDERS, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inv, null));
		return tag;
	}
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		
		if (hand == EnumHand.MAIN_HAND) {
			player.openGui(RealFilingCabinet.instance, 1, world, 0, 0, 0);
			return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}
		return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
	}
	
	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float x, float y, float z) {
		
		ItemStack heldStack = player.getHeldItem(hand);
		IItemHandlerModifiable suitcaseInv = getSuitcaseInv(heldStack);
		TileEntity tile = world.getTileEntity(pos);
		
		if (tile instanceof TileEntityRFC) {
			if (!world.isRemote) {
				TileEntityRFC tfc = (TileEntityRFC)tile;
				if (!tfc.isOpen)
					return EnumActionResult.FAIL;
				
				boolean flag = ItemHandlerHelper.calcRedstoneFromInventory(suitcaseInv) > 0;
				if (flag) {
					if (ItemHandlerHelper.calcRedstoneFromInventory(tfc.getInventory()) > 0)
						return EnumActionResult.FAIL;
					
					for (int i = 0; i < suitcaseInv.getSlots(); i++) {
						ItemStack stack = suitcaseInv.getStackInSlot(i);
						if (!stack.isEmpty()) {
							tfc.getInventory().setStackInSlot(i, stack);
							suitcaseInv.extractItem(i, 1, false);
						}
					}
				} else {
					for (int j = 0; j < tfc.getInventory().getSlots(); j++) {
						ItemStack stack = tfc.getInventory().getFolder(j);
						if (!stack.isEmpty()) {
							suitcaseInv.insertItem(j, stack, false);
							tfc.getInventory().setStackInSlot(j, ItemStack.EMPTY);
						}
					}
				}
				tfc.markBlockForUpdate();
			}
			return EnumActionResult.SUCCESS;
		}
		ItemStack folder = suitcaseInv.getStackInSlot(StorageUtils.getIndex(heldStack));
		if (folder.getItem() instanceof IFolder) {
			ItemStack savedSuitcase = player.getHeldItem(hand);
			EnumActionResult ear = ((IFolder)folder.getItem()).placeObject(folder, player, world, pos, hand, side, x, y, z);
			player.setHeldItem(hand, savedSuitcase);
			
			return ear;
		}
		return EnumActionResult.PASS;
//		Object obj = FolderUtils.get(suitcaseInv.getStackInSlot(StorageUtils.getIndex(heldStack))).getObject();
//		if (obj instanceof ItemStack) {
//			ItemStack stackToPlace = new ItemStack(((ItemStack)obj).getItem(), 1, ((ItemStack)obj).getItemDamage());
//			if (!stackToPlace.isEmpty() && stackToPlace.getItem() instanceof ItemBlock) {
//				ItemStack savedSuitcase = player.getHeldItem(hand);
//				
//				player.setHeldItem(hand, stackToPlace);
//				EnumActionResult ear = stackToPlace.onItemUse(player, world, pos, hand, side, x, y, z);
//				player.setHeldItem(hand, savedSuitcase);
//				
//				if (ear == EnumActionResult.SUCCESS) {
//					if (!world.isRemote && !player.capabilities.isCreativeMode) {
//						FolderUtils.get(suitcaseInv.getStackInSlot(StorageUtils.getIndex(heldStack))).remove(1);
//					}
//					return EnumActionResult.SUCCESS;
//				}
//			}
//		}
//		return EnumActionResult.PASS;
	}
	
	@Nonnull
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound tag) {
		
		return new InventorySuitcase.InvProvider();
	}
	
	public static IItemHandlerModifiable getSuitcaseInv(ItemStack stack) {
		
		return (IItemHandlerModifiable)stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
	}
}

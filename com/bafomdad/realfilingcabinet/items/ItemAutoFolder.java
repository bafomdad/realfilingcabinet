package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import com.bafomdad.realfilingcabinet.LogRFC;
import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityProviderFolder;
import com.bafomdad.realfilingcabinet.utils.NBTUtils;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemAutoFolder extends Item implements IFolder {
	
	public ItemAutoFolder() {
		
		setRegistryName("folder_auto");
		setTranslationKey(RealFilingCabinet.MOD_ID + ".folder_auto");
		setHasSubtypes(true);
		setMaxStackSize(1);
	}
	
	@Override
	public NBTTagCompound getNBTShareTag(ItemStack stack) {
		
		if(!stack.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null))
			return super.getNBTShareTag(stack);
		
		NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound().copy() : new NBTTagCompound();
		tag.setTag("folderCap", stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null).serializeNBT());
		return tag;
	}
	
	@Override
	public void addInformation(ItemStack stack, World player, List<String> list, ITooltipFlag whatisthis) {
		
		if(stack.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null)) // Direction doesn't really matter here.
			stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null).addTooltips(player, list, whatisthis);
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		
		if(!stack.hasCapability(CapabilityProviderFolder.FOLDER_CAP, null))
			return ItemStack.EMPTY;
		
		CapabilityFolder cap = stack.getCapability(CapabilityProviderFolder.FOLDER_CAP, null);
		long count = cap.getCount();
		long extract = 0;
		if (count > 0 && cap.isItemStack())
			extract = Math.min(cap.getItemStack().getMaxStackSize(), count);
		
		if (NBTUtils.getBoolean(stack, StringLibs.RFC_TAPED, false))
			return ItemStack.EMPTY;
		
		ItemStack copy = stack.copy();

		ItemFolder.remove(copy, extract);
		
		return copy;
	}
	
	@Override
	public boolean hasContainerItem(ItemStack stack) {
		
		return !getContainerItem(stack).isEmpty();
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		ItemStack stack = player.getHeldItem(hand);
		if (ItemFolder.getObject(stack) != null) {
			if (((ItemStack)ItemFolder.getObject(stack)).getItem() instanceof ItemBlock) {	
				long count = ItemFolder.getFileSize(stack);
				if (count > 0) {
					ItemStack stackToPlace = new ItemStack(((ItemStack)ItemFolder.getObject(stack)).getItem(), 1, ((ItemStack)ItemFolder.getObject(stack)).getItemDamage());
					ItemStack savedfolder = player.getHeldItem(hand);
					
					player.setHeldItem(hand, stackToPlace);
					EnumActionResult ear = stackToPlace.onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ);
					player.setHeldItem(hand, savedfolder);
					
					if (ear == EnumActionResult.SUCCESS) {
						if (!player.capabilities.isCreativeMode)
							ItemFolder.remove(stack, 1);
						
						return EnumActionResult.SUCCESS;
					}
				}
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	public ItemStack isFolderEmpty(ItemStack stack) {

		return new ItemStack(RFCItems.autoFolder, 1, 0);
	}
}

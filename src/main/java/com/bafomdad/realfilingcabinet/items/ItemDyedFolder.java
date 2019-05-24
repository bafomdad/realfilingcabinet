package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import com.bafomdad.realfilingcabinet.ConfigRFC;
import com.bafomdad.realfilingcabinet.LogRFC;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.api.ISubModel;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDyedFolder extends ItemAbstractFolder implements ISubModel, IFolder {
	
	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		
		ItemStack item = ItemStack.EMPTY;
		CapabilityFolder cap = FolderUtils.get(stack).getCap();
		if (cap == null) return item;
		
		long count = FolderUtils.get(stack).getFileSize();
		long extract = 0;
		if (count > 0 && cap.isItemStack())
			extract = Math.min(cap.getItemStack().getMaxStackSize(), count);
			
		item = stack.copy();
		FolderUtils.get(item).remove(extract);
		
		return item;
	}
	
	@Override
	public boolean hasContainerItem(ItemStack stack) {
		
		return !getContainerItem(stack).isEmpty();
	}
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		
		return getTranslationKey() + "." + EnumDyeColor.values()[stack.getItemDamage()].getName().toLowerCase();
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
	
		ItemStack folder = player.getHeldItem(hand);
		return this.placeObject(folder, player, world, pos, hand, side, hitX, hitY, hitZ);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerSubModels(Item item) {

		for (int i = 0; i < EnumDyeColor.values().length; ++i)
			ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

	@Override
	public ItemStack getEmptyFolder(ItemStack stack) {

		return new ItemStack(RFCItems.EMPTYDYEDFOLDER, 1, stack.getItemDamage());
	}
	
	@Override
	public EnumActionResult placeObject(ItemStack folder, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		Object obj = FolderUtils.get(folder).getObject();
		if (obj instanceof ItemStack) {
			if (((ItemStack)obj).getItem() instanceof ItemBlock) {
				long count = FolderUtils.get(folder).getFileSize();
				
				if (count > 0) {
					ItemStack stackToPlace = new ItemStack(((ItemStack)obj).getItem(), 1, ((ItemStack)obj).getItemDamage());
					ItemStack savedFolder = player.getHeldItem(hand);
					
					player.setHeldItem(hand, stackToPlace);
					EnumActionResult ear = stackToPlace.onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ);
					player.setHeldItem(hand, savedFolder);
					
					if (ear == EnumActionResult.SUCCESS) {
						if (!player.capabilities.isCreativeMode)
							FolderUtils.get(folder).remove(1);
						
						return EnumActionResult.SUCCESS;
					}
				}
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	public Object insertIntoFolder(ItemStack folder, Object toInsert, boolean simulate) {
		
		CapabilityFolder cap = FolderUtils.get(folder).getCap();
		
		if (cap == null) return null;
		if (!(toInsert instanceof ItemStack) || !cap.isItemStack()) return null;
		
		ItemStack stack = (ItemStack)toInsert;
		if (!ItemStack.areItemsEqual(stack, cap.getItemStack())) return null;
		
		long newCount = Math.min(cap.getCount() + stack.getCount(), ConfigRFC.folderSizeLimit);
		long remainder = ConfigRFC.folderSizeLimit - cap.getCount();
		stack.setCount(stack.getCount() - (int)remainder);
		if (!simulate)
			cap.setCount(newCount);
		
		return toInsert;
	}

	@Override
	public Object extractFromFolder(ItemStack folder, long amount, boolean simulate) {

		CapabilityFolder cap = FolderUtils.get(folder).getCap();
		ItemStack items = cap.getItemStack();
		items.setCount((int)Math.min(cap.getCount(), amount));
		
		if (!simulate)
			cap.setCount(cap.getCount() - items.getCount());

		return items.copy();	
	}
}

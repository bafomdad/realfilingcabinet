package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.realfilingcabinet.LogRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;

public abstract class ItemAbstractFolder extends Item {
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag whatisthis) {
		
		FolderUtils.get(stack).addTooltips(list);
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(StringLibs.RFC_SLOTINDEX)) {
			list.add(TextFormatting.GOLD + "Current slot: " + stack.getTagCompound().getInteger(StringLibs.RFC_SLOTINDEX));
		}
	}

	@Override
	public NBTTagCompound getNBTShareTag(ItemStack stack) {
		
		CapabilityFolder cap = FolderUtils.get(stack).getCap();
		if (cap == null) return super.getNBTShareTag(stack);
		
		NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound().copy() : new NBTTagCompound();
		tag.setTag("folderCap", cap.serializeNBT());
		LogRFC.debug("Sharing tag: " + tag.toString());
		return tag;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
		
		CapabilityFolder cap = FolderUtils.get(stack).getCap();
		if (cap != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("folderCap", 10)) {
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag("folderCap");
			LogRFC.debug("Deserializing: " + tag.toString());
			cap.deserializeNBT(tag);
			stack.getTagCompound().removeTag("folderCap");
			
			if (stack.getTagCompound().getSize() <= 0)
				stack.setTagCompound(null);
		}
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldstack, ItemStack newstack, boolean slotchanged) {
		
		return oldstack.getItem() != newstack.getItem();
	}
}

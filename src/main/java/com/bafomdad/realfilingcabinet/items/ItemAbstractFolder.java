package com.bafomdad.realfilingcabinet.items;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.realfilingcabinet.LogRFC;
import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder;
import com.bafomdad.realfilingcabinet.items.capabilities.CapabilityProviderFolder;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;

public abstract class ItemAbstractFolder extends Item {
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag whatisthis) {
		
		boolean crouching = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
		FolderUtils.get(stack).addTooltips(list, crouching);
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
	public void readNBTShareTag(ItemStack stack, NBTTagCompound nbt) {
		
		CapabilityFolder cap = FolderUtils.get(stack).getCap();
		if (cap != null && nbt != null && nbt.hasKey("folderCap", 10)) {
			NBTTagCompound tag = nbt.getCompoundTag("folderCap");
			LogRFC.debug("Deserializing: " + tag.toString());
			cap.deserializeNBT(tag);
			nbt.removeTag("folderCap");
		}
		super.readNBTShareTag(stack, nbt);
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldstack, ItemStack newstack, boolean slotchanged) {
		
		return oldstack.getItem() != newstack.getItem();
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound tag) {
		
		// function to convert from the capability dispatcher used by the AttachCapabilitiesEvent to this one
		if (tag != null && tag.hasKey(CapabilityProviderFolder.FOLDER_ID.toString(), 10)) {
			if (!stack.isEmpty()) {
				return new CapabilityProviderFolder(stack, tag);
			}
		}
		return new CapabilityProviderFolder(stack);
	}
}

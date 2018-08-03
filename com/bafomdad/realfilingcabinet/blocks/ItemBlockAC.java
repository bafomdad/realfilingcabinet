package com.bafomdad.realfilingcabinet.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import thaumcraft.api.aspects.Aspect;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemAspectFolder;

public class ItemBlockAC extends ItemBlock {

	public ItemBlockAC(Block block) {
		
		super(block);
	}
	
	@Override
	public void addInformation(ItemStack stack, World player, List list, ITooltipFlag whatisthis) {
		
		if (stack.hasTagCompound()) {
			list.add(TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".itemblockrfc"));
			if ((Keyboard.isKeyDown(42)) || (Keyboard.isKeyDown(54)))
				listAspects(stack, list);
			else if (list.size() > 2)
				list.remove(2);
		}
	}
	
	private void listAspects(ItemStack stack, List list) {
		
		NBTTagList tagList = stack.getTagCompound().getTagList("inventory", 10);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound itemTag = tagList.getCompoundTagAt(i);
			ItemStack folder = new ItemStack(itemTag);
			if (!folder.isEmpty() && folder.getItem() == RFCItems.aspectFolder) {
				Aspect asp = ItemAspectFolder.getAspectFromFolder(folder);
				int count = ItemAspectFolder.getAspectCount(folder);
				if (asp != null)
					list.add(asp.getName() + " x" + count);
			}
		}
	}
}

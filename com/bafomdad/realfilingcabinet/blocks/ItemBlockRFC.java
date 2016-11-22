package com.bafomdad.realfilingcabinet.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.lwjgl.input.Keyboard;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

public class ItemBlockRFC extends ItemBlock {

	public ItemBlockRFC(Block block) {
		
		super(block);
	}
	
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean whatisthis) {
		
		if (stack.hasTagCompound())
		{
			list.add(TextHelper.localize("tooltip." + RealFilingCabinet.MOD_ID + ".itemblockrfc"));
			if ((Keyboard.isKeyDown(42)) || (Keyboard.isKeyDown(54))) {
				listItems(stack, list);
			} else if (list.size() > 2) {
				list.remove(2);
			}
		}
	}
	
	private void listItems(ItemStack stack, List list) {
		
		NBTTagList tagList = stack.getTagCompound().getTagList("inventory", 10);
		for (int i = 0; i < tagList.tagCount(); i++)
		{
			NBTTagCompound itemTag = tagList.getCompoundTagAt(i);
			ItemStack folder = new ItemStack(itemTag);
			if (folder != ItemStack.field_190927_a && folder.getItem() == RFCItems.folder)
			{
				String name = "";
				if (ItemFolder.getObject(folder) instanceof ItemStack)
					name = ((ItemStack)ItemFolder.getObject(folder)).getDisplayName();
				else if (ItemFolder.getObject(folder) instanceof String)
					name = (String)ItemFolder.getObject(folder);
				
				long count = ItemFolder.getFileSize(folder);
				
				list.add(TextHelper.format(count) + " " + name);
			}
		}
	}
}

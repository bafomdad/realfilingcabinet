package com.bafomdad.realfilingcabinet.items.itemblocks;

import java.util.List;

import org.lwjgl.input.Keyboard;

import thaumcraft.api.aspects.Aspect;

import com.bafomdad.realfilingcabinet.helpers.StringLibs;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemAspectFolder;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class ItemBlockAspect extends ItemBlock {

	public ItemBlockAspect(Block block) {
		
		super(block);
	}
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag whatisthis) {
		
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("inventory")) {
			list.add(new TextComponentTranslation(StringLibs.TOOLTIP + ".extrainfo").getFormattedText());
			if (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54))
				listItems(stack, list);
		}
	}
	
	private void listItems(ItemStack stack, List<String> list) {
		
		NBTTagCompound invTag = stack.getTagCompound().getCompoundTag("inventory");
		NBTTagList tagList = invTag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound itemTag = tagList.getCompoundTagAt(i);
			ItemStack folder = new ItemStack(itemTag);
			if (!folder.isEmpty() && folder.getItem() == RFCItems.FOLDER_ASPECT) {
				Aspect asp = ItemAspectFolder.getAspectFromFolder(folder);
				int count = ItemAspectFolder.getAspectCount(folder);
				if (asp != null)
					list.add(asp.getName() + " x" + count);
			}
		}
	}
}

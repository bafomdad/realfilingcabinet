package com.bafomdad.realfilingcabinet.commands;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemEmptyFolder;
import com.bafomdad.realfilingcabinet.items.ItemFolder;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;

public class CommandFolderItem {

	public static void setItem(EntityPlayer player, String object, int meta) {
		
		ItemStack folder = player.getHeldItemMainhand();
		if (folder == null || (folder != null && !(folder.getItem() instanceof ItemEmptyFolder))) {
			player.sendMessage(new TextComponentString(TextHelper.localizeCommands("notEmptyFolder")));
			return;
		}
		if (folder.getItemDamage() != 0) {
			player.sendMessage(new TextComponentString(TextHelper.localizeCommands("notNormalFolder")));
			return;
		}
		if (Item.getByNameOrId(object) != null) {
			Item item = Item.getByNameOrId(object);
			ItemStack toStore = new ItemStack(item, 1, meta);
			ItemStack newFolder = new ItemStack(RFCItems.folder, 1, 0);
			ItemFolder.setObject(newFolder, toStore);
			player.setHeldItem(EnumHand.MAIN_HAND, newFolder);
			return;
		}
		if (Block.getBlockFromName(object) != null) {
			Block block = Block.getBlockFromName(object);
			ItemStack toStore = new ItemStack(block, 1, meta);
			ItemStack newFolder = new ItemStack(RFCItems.folder, 1, 0);
			ItemFolder.setObject(newFolder, toStore);
			player.setHeldItem(EnumHand.MAIN_HAND, newFolder);
			return;
		}
		else
			player.sendMessage(new TextComponentString(TextHelper.localizeCommands("noItemOrBlockFound")));
	}
}

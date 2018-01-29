package com.bafomdad.realfilingcabinet.commands;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
import com.bafomdad.realfilingcabinet.items.ItemManaFolder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class CommandFolderCount {

	public static void setCount(EntityPlayer player, String count) {
		
		ItemStack folder = player.getHeldItemMainhand();
		if (folder == null || (folder != null &&  !(folder.getItem() instanceof IFolder))) {
			player.sendMessage(new TextComponentString(TextHelper.localizeCommands("notEmptyFolder")));
			return;
		}
		if (folder.getItem() instanceof ItemManaFolder) {
			player.sendMessage(new TextComponentString(TextHelper.localizeCommands("errorManaFolder")));
			return;
		}	
		try {
			long size = Long.parseLong(count);
			if (!player.world.isRemote)
				ItemFolder.setFileSize(folder, size);
			
		} catch (Exception e) {
			player.sendMessage(new TextComponentString(TextHelper.localizeCommands("errorParseLong")));
		}
	}
}

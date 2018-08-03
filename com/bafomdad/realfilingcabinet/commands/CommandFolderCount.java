package com.bafomdad.realfilingcabinet.commands;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.integration.BotaniaRFC;
import com.bafomdad.realfilingcabinet.items.ItemFolder;
//import com.bafomdad.realfilingcabinet.items.ItemManaFolder;



import com.bafomdad.realfilingcabinet.items.ItemManaFolder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class CommandFolderCount {

	public static void setCount(EntityPlayer player, String count) {
		
		ItemStack folder = player.getHeldItemMainhand();
		if (folder.isEmpty() || (!folder.isEmpty() && !(folder.getItem() instanceof IFolder))) {
			player.sendMessage(new TextComponentString(TextHelper.localizeCommands("notEmptyFolder")));
			return;
		}	
		try {
			long size = Long.parseLong(count);
			if (!player.world.isRemote) {
				if (folder.getItem() == RFCItems.folder)
					ItemFolder.setFileSize(folder, size);
				if (folder.getItem() == BotaniaRFC.manaFolder)
					ItemManaFolder.setManaSize(folder, (int)size);
			}
			
		} catch (Exception e) {
			player.sendMessage(new TextComponentString(TextHelper.localizeCommands("errorParseLong")));
		}
	}
}

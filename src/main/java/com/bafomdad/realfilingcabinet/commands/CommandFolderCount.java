package com.bafomdad.realfilingcabinet.commands;

import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemAspectFolder;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;
import com.bafomdad.realfilingcabinet.utils.ManaStorageUtils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class CommandFolderCount extends CommandBase {

	@Override
	public String getName() {

		return "setcount";
	}

	@Override
	public String getUsage(ICommandSender sender) {

		return "commands.realfilingcabinet.usage.setcount";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		if (args.length != 1)
			throw new WrongUsageException(getUsage(sender));
		
		EntityPlayer player = (EntityPlayer)sender;
		ItemStack folder = player.getHeldItemMainhand();
		
		if (!folder.isEmpty() && folder.getItem() instanceof IFolder) {
			long count = Long.parseLong(args[0]);
			if (!player.world.isRemote) {
				if (folder.getItem() == RFCItems.FOLDER_MANA)
					ManaStorageUtils.setManaSize(folder, (int)count);
				if (folder.getItem() == RFCItems.FOLDER_ASPECT)
					ItemAspectFolder.setAspectCount(folder, (int)count);
				else
					FolderUtils.get(folder).setFileSize(count);
			}
		}
		else throw new CommandException("commands.realfilingcabinet.error.not_a_folder");
	}
}

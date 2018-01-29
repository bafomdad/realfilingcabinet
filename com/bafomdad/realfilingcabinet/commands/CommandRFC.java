package com.bafomdad.realfilingcabinet.commands;

import java.util.ArrayList;
import java.util.List;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.helpers.TextHelper;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandRFC extends CommandBase {
	
	private final List<String> aliases = new ArrayList<String>();
	
	public CommandRFC() {
		
		aliases.add("RealFilingCabinet");
		aliases.add("realfilingcabinet");
		aliases.add("rfc");
	}

	@Override
	public String getName() {

		return "/rfc";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		
		return 2;
	}

	@Override
	public String getUsage(ICommandSender sender) {

		return getName();
	}
	
	@Override
	public List<String> getAliases() {
		
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		if (args.length > 0) {
			if (args[0].contains("setcount") && args.length > 1 && sender instanceof EntityPlayer)
				CommandFolderCount.setCount((EntityPlayer)sender, args[1]);
			if (args[0].contains("setitem") && args.length > 1 && sender instanceof EntityPlayer) {
				if (args.length > 2) {
					try {
						int meta = Integer.parseInt(args[2]);
						CommandFolderItem.setItem((EntityPlayer)sender, args[1], meta);
					} catch (Exception e) {
						sender.sendMessage(new TextComponentString(TextHelper.localizeCommands("errorNumberArg")));
					}
				}
				else
					CommandFolderItem.setItem((EntityPlayer)sender, args[1], 0);
			}
			else if (args.length < 2)
				sender.sendMessage(new TextComponentString(TextHelper.localizeCommands("errorNoArgs")));
		}
		else
			sender.sendMessage(new TextComponentString(TextHelper.localizeCommands("help")));
	}
}

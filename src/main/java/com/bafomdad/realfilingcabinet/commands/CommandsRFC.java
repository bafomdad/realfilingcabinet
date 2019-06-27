package com.bafomdad.realfilingcabinet.commands;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

public class CommandsRFC extends CommandTreeBase {
	
	public CommandsRFC() {
		
		addSubcommand(new CommandFolderCount());
		addSubcommand(new CommandSetFolder());
	}

	@Override
	public String getName() {

		return "rfc";
	}

	@Override
	public String getUsage(ICommandSender sender) {

		return "commands.realfilingcabinet.help";
	}
}

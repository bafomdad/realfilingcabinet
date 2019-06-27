package com.bafomdad.realfilingcabinet.commands;

import com.bafomdad.realfilingcabinet.api.IEmptyFolder;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.utils.FolderUtils;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class CommandSetFolder extends CommandBase {

	@Override
	public String getName() {

		return "setitem";
	}

	@Override
	public String getUsage(ICommandSender sender) {

		return "commands.realfilingcabinet.usage.setitem";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		if (args.length != 1) {
			throw new WrongUsageException(getUsage(sender));
		}
		EntityPlayer player = (EntityPlayer)sender;
		ItemStack folder = player.getHeldItemMainhand();
		if (!folder.isEmpty() && ((folder.getItem() == RFCItems.EMPTYFOLDER && folder.getItemDamage() == 0) || folder.getItem() == RFCItems.EMPTYDYEDFOLDER)) {
			ResourceLocation res = new ResourceLocation(args[0]);
			Item itemEntry = ForgeRegistries.ITEMS.getValue(res);
			Block blockEntry = ForgeRegistries.BLOCKS.getValue(res);
			int meta = 0;
			
			if (args.length > 1)
				meta = Integer.parseInt(args[1]);
			
			if (itemEntry == null && blockEntry == null)
				throw new CommandException("commands.realfilingcabinet.error.no_item_or_block_found");

			ItemStack stack = ItemStack.EMPTY;
			if (itemEntry != null && itemEntry != Items.AIR)
				stack = new ItemStack(itemEntry, 1, meta);
			
			if (blockEntry != null && blockEntry != Blocks.AIR)
				stack = new ItemStack(blockEntry, 1, meta);
			
			if (stack.isEmpty())
				throw new CommandException("commands.realfilingcabinet.error.no_item_or_block_found");
			
			ItemStack newFolder = ((IEmptyFolder)folder.getItem()).getFilledFolder(folder);
			FolderUtils.get(newFolder).setObject(stack);
			player.setHeldItem(EnumHand.MAIN_HAND, newFolder);
		}
		else
			throw new CommandException("commands.realfilingcabinet.error.not_an_empty_normal_or_dyed_folder");
	}
}

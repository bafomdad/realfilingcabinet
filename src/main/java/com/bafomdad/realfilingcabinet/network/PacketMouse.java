package com.bafomdad.realfilingcabinet.network;

import com.bafomdad.realfilingcabinet.helpers.enums.FolderType;
import com.bafomdad.realfilingcabinet.init.RFCItems;
import com.bafomdad.realfilingcabinet.items.ItemSuitcase;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketMouse implements IMessage {
	
	private int wheel;
	
	public PacketMouse() {}
	
	public PacketMouse(int wheel) {
		
		this.wheel = wheel;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		wheel = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {

		buf.writeInt(wheel);
	}
	
	public static class Handler implements IMessageHandler<PacketMouse, IMessage> {

		@Override
		public IMessage onMessage(PacketMouse message, MessageContext ctx) {

			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				
				@Override
				public void run() {
					
					handle(message, ctx);
				}
			});
			return null;
		}
		
		private void handle(PacketMouse message, MessageContext ctx) {
			
			EntityPlayerMP player = ctx.getServerHandler().player;
			ItemStack stack = player.getHeldItemMainhand();
			if (!stack.isEmpty() && (stack.getItem() == RFCItems.SUITCASE || (stack.getItem() == RFCItems.FOLDER && stack.getItemDamage() == FolderType.ENDER.ordinal()))) {
				StorageUtils.cycleIndex(stack, message.wheel);
			}
		}
	}
}